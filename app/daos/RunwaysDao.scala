package daos

import java.io.File

import com.github.tototoshi.csv.CSVReader
import com.google.inject.{Inject, Singleton}
import models._
import play.api.{Application, Environment}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import sql.drivers.ExtendedPGProfile.api._
import sql.queries.{RunwayRow, RunwaysQuery}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by caeus on 24/06/17.
  */
@Singleton
class RunwaysDao @Inject()(query:RunwaysQuery,
                           val dbConfigProvider: DatabaseConfigProvider,
                           environment: Environment)
                          (implicit executionContext: ExecutionContext)extends HasDatabaseConfigProvider[JdbcProfile] {


  def init={

    val file = new File(environment.classLoader.getResource("runways.csv").toURI)
    CSVReader.open(file).allWithHeaders().map {
      row =>
        row.mapValues {
          value => if (value.isEmpty) None else Some(value)
        }
    }.map {
      row =>
        //I use get because I literally want this to fail
        //I don't want to let pass any empty string
        RunwayRow(id = row("id").get.toLong: Long,
          airport = RunwayAirport(
            ref = row("airport_ref").get.toLong,
            ident = row("airport_ident").get
          ): RunwayAirport,
          lengthFt = row("length_ft").map(_.toDouble),
          widthFt = row("width_ft").map(_.toDouble),
          surface = row("surface"),
          lighted = row("lighted").get.toDouble: Double,
          closed = row("closed").get.toDouble: Double,
          le = RunwayLe(
            ident = row("le_ident"),
            latitude = row("le_latitude_deg").map(_.toDouble),
            longitude = row("le_longitude_deg").map(_.toDouble),
            elevation = row("le_elevation_ft").map(_.toDouble),
            headingT = row("le_heading_degT").map(_.toDouble),
            displacedThreshold = row("le_displaced_threshold_ft").map(_.toDouble)),
          he = RunwayHe(
            ident = row("he_ident"),
            latitude = row("he_latitude_deg").map(_.toDouble),
            longitude = row("he_longitude_deg").map(_.toDouble),
            elevation = row("he_elevation_ft").map(_.toDouble),
            headingT = row("he_heading_degT").map(_.toDouble),
            displacedThreshold = row("he_displaced_threshold_ft").map(_.toDouble)))
    }match {
      case data => insert(data)
    }
  }

  private def insert(runways: Seq[RunwayRow]):Future[Omit] = {
    db.run(query ++= runways).map(_ => Omit)
  }

}
