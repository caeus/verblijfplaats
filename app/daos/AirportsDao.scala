package daos

import java.io.File

import com.github.tototoshi.csv.CSVReader
import com.google.inject.{Inject, Singleton}
import models._
import play.api.{Application, Environment}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import sql.drivers.ExtendedPGProfile.api._
import sql.queries.{AirportRow, AirportsQuery}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by caeus on 24/06/17.
  */
@Singleton
class AirportsDao @Inject()(query: AirportsQuery,
                            val dbConfigProvider: DatabaseConfigProvider,
                            environment: Environment)
                           (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  def init = {


    val file = new File(environment.classLoader.getResource("airports.csv").toURI)
    CSVReader.open(file).allWithHeaders().map {
      row =>
        row.mapValues {
          value => if (value.isEmpty) None else Some(value)
        }
    }.map {
      row =>
        //I use get because I literally want this to fail
        //I don't want to let pass any empty string
        AirportRow(id = row("id").get.toLong: Long,
          ident = row("ident").get: String,
          kind = row("type").get: String,
          name = row("name").get: String,
          location = AirportLocation(
            latitude = row("latitude_deg").get.toDouble,
            longitude = row("longitude_deg").get.toDouble,
            elevation = row("elevation_ft").map(_.toDouble)
          ): AirportLocation,
          continent = row("continent").get: String,
          isoCountry = row("iso_country").get: String,
          isoRegion = row("iso_region").get: String,
          municipality = row("municipality"),
          scheduledService = !row("type").get.equals("no"): Boolean,
          codes = AirportCodes(
            gps = row("gps_code"),
            iata = row("iata_code"),
            local = row("local_code")
          ): AirportCodes,
          homeLink = row("home_link"),
          wikipediaLink = row("wikipedia_link"),
          keywords = row("keywords").map {
            value => value.split("\\s+").toList
          }.getOrElse(Nil): List[String])
    } match {
      case data => insert(data)
    }
  }

  private def insert(airports: Seq[AirportRow]): Future[Omit] = {
    db.run(query ++= airports).map(_ => Omit)
  }

}
