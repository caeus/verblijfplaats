package daos

import java.io.File

import com.github.tototoshi.csv.CSVReader
import com.google.inject.{Inject, Singleton}
import models._
import play.api.Environment
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import sql.drivers.ExtendedPGProfile.api._
import sql.queries._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by caeus on 24/06/17.
  *
  * Daos are for serializing, and deserializing and communicating with
  * databases, for me. A lot of code of that in here, mainly because of
  * the need of populating it with CSV files
  */
@Singleton
class CountriesDao @Inject()(query: CountriesQuery,
                             airportsQuery: AirportsQuery,
                             runwaysQuery: RunwaysQuery,
                             val dbConfigProvider: DatabaseConfigProvider,
                             environment: Environment)
                            (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  def runwayModes: Future[Seq[RunwayMode]] = {
    val action = runwaysQuery.filter(_.leIdent.isDefined)
      .groupBy(_.leIdent)
      .map {
        case (leIdent, group) =>
          leIdent -> group.length
      }
      .sortBy {
        //I prefer destructuring than ._1 ._2 ._ bla bla bla access, because it's way more readable
        case (_, count) => count.desc
      }
      .take(10).result
    db.run(action)
      .map {
        data =>
          data.map {
            case (Some(ident), count) => RunwayMode(ident, count)
            case _ => throw new IllegalStateException("Database was probably edited during this query")
          }
      }

  }

  def airportCounts(limit: Int, asc: Boolean): Future[Seq[CountryAirportCount]] = {
    val counts = airportsQuery.groupBy(_.isoCountry).map {
      case (country, results) =>
        country -> results.length
    }
    val action = query.joinLeft(counts).on {
      case (country, (countryCode, _)) => countryCode === country.code
    }.sortBy {
      case (_, right) if asc => right.map(_._2).getOrElse(0).asc
      case (_, right) => right.map(_._2).getOrElse(0).desc
    }.take(limit).result

    db.run(action)
      .map {
        results =>
          results.map {
            case (row, right) =>
              CountryAirportCount(
                id = row.id: Long,
                code = row.code: String,
                name = row.name: String,
                airportCount = right.map(_._2).getOrElse(0): Int
              )
          }
      }
  }


  private def deserializeRunway(runway: RunwayRow): Runway = {
    import runway._
    Runway(id: Long,
      lengthFt: Option[Double],
      widthFt: Option[Double],
      surface: Option[String],
      lighted: Double,
      closed: Double,
      le: RunwayLe,
      he: RunwayHe)
  }

  private def deserializeAirport(airport: AirportRow, runways: Seq[RunwayRow]): Airport = {
    Airport(id = airport.id: Long,
      ident = airport.ident: String,
      kind = airport.kind: String,
      name = airport.name: String,
      location = airport.location: AirportLocation,
      continent = airport.continent: String,
      isoRegion = airport.isoRegion: String,
      municipality = airport.municipality: Option[String],
      scheduledService = airport.scheduledService: Boolean,
      codes = airport.codes: AirportCodes,
      homeLink = airport.homeLink: Option[String],
      wikipediaLink = airport.wikipediaLink: Option[String],
      keywords = airport.keywords: List[String],
      runways = runways.map(deserializeRunway): Seq[Runway])

  }

  private def deserializeCountry(
                                  country: CountryRow,
                                  airports: Seq[(AirportRow, Seq[RunwayRow])]
                                ): Country = {


    Country(id = country.id: Long,
      code = country.code: String,
      name = country.name: String,
      continent = country.continent: String,
      wikipediaLink = country.wikipediaLink: String,
      keywords = country.keywords: List[String],
      airports = airports.map {
        case (airport, runways) => deserializeAirport(airport, runways)
      }: Seq[Airport])
  }

  private def fullQuery = query.join(airportsQuery)
    .on(_.code === _.isoCountry)
    .join(runwaysQuery).on {
    case ((_, airport), runway) =>
      airport.id === runway.airportRef
  }.map {
    case ((country, airport), runway) =>
      (country, airport, runway)
  }

  def byCode(code: String): Future[Option[Country]] = {
    val result = fullQuery.filter {
      case (country, _, _) =>
        country.code === code
    }.result
    db.run(result).map {
      case data if data.nonEmpty =>
        val (country, _, _) = data.head
        val airports = data.groupBy {
          case (_, airport, _) =>
            airport
        }.mapValues {
          tuples =>
            tuples.map(_._3)
        }.toSeq

        Some(deserializeCountry(country, airports))
      case _ => None
    }
  }

  def query(q: String): Future[Seq[CountryDescriptor]] = {
    val text = q.toLowerCase()
    val action = query.filter {
      schema =>
        schema.code.toLowerCase.like(s"%$text%") || schema.name.toLowerCase.like(s"%$text%")
    }.result
    db.run(action)
      .map {
        rows =>
          rows.map {
            row =>
              import row._
              CountryDescriptor(id: Long,
                code: String,
                name: String,
                continent: String,
                wikipediaLink: String,
                keywords: List[String])
          }
      }
  }


  def init = {


    val file = new File(environment.classLoader.getResource("countries.csv").toURI)
    CSVReader.open(file).allWithHeaders().map {
      row =>
        row.mapValues {
          value => if (value.isEmpty) None else Some(value)
        }
    }.map {
      row =>
        //I use get because I literally want this to fail
        //I don't want to let pass any empty string
        CountryRow(id = row("id").get.toLong,
          code = row("code").get: String,
          name = row("name").get: String,
          continent = row("continent").get: String,
          wikipediaLink = row("wikipedia_link").get: String,
          keywords = row("keywords").map {
            value => value.split("\\s+").toList
          }.getOrElse(Nil): List[String]
        )
    } match {
      case data => insert(data)
    }
  }


  private def insert(countries: Seq[CountryRow]): Future[Omit] = {
    db.run(query ++= countries).map(_ => Omit)
  }

  def runwayHistograms: Future[Seq[CountryRunwayHistogram]] = {
    val histograms = fullQuery.groupBy {
      case (country, _, runway) =>
        //I don't like doind this, but for the sake of the report
        (country.code, runway.surface.getOrElse("unknown"))
    }.map {
      case ((countryCode, surface), query) =>
        (countryCode, surface, query.length)
    }
    val action = histograms.join(query).on {
      case ((countryCode, _, _), country) => countryCode === country.code
    }
      .map {
        case ((countryCode, surface, count), country) =>
          (country, surface, count)
      }.result
    db.run(action).map {
      results =>
        results.groupBy(_._1).toSeq.map {
          case (countryRow, data) =>
            CountryRunwayHistogram(id = countryRow.id: Long,
              code = countryRow.code: String,
              name = countryRow.name: String,
              data = data.foldLeft(Map.empty[String, Int]) {
                case (map, (_, surface, count)) =>
                  //I could've added, so not to override a previous surface count
                  //but that will never happen because they are grouped by surface
                  //so each kind of surface will appear only once
                  map + (surface -> count)
              }: Map[String, Int])
        }
    }
  }

}
