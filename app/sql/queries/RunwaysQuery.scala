package sql.queries

import com.google.inject.{Inject, Singleton}
import models._
import slick.lifted.ProvenShape
import sql.drivers.ExtendedPGProfile.api._

@Singleton
class RunwaysQuery @Inject()(airportsQuery: AirportsQuery) extends TableQuery(new RunwaySchema(_, airportsQuery))

/**
  * Created by caeus on 24/06/17.
  */

case class RunwayRow(id: Long,
                     airport: RunwayAirport,
                     lengthFt: Option[Double],
                     widthFt: Option[Double],
                     surface: Option[String],
                     lighted: Double,
                     closed: Double,
                     le: RunwayLe,
                     he: RunwayHe)

class RunwaySchema(tag: Tag, airportsQuery: AirportsQuery) extends Table[RunwayRow](tag, "runway") {
  def id = column[Long]("id", O.PrimaryKey)

  def airportRef = column[Long]("airport_ref")

  def airportIdent = column[String]("airport_ident")

  def lengthFt = column[Option[Double]]("length_ft")

  def widthFt = column[Option[Double]]("width_ft")

  def surface = column[Option[String]]("surface")

  def lighted = column[Double]("lighted")

  def closed = column[Double]("closed")

  def leIdent = column[Option[String]]("le_ident")

  def leLatitudeDeg = column[Option[Double]]("le_latitude_deg")

  def leLongitudeDeg = column[Option[Double]]("le_longitude_deg")

  def leElevationFt = column[Option[Double]]("le_elevation_ft")

  def leHeadingDegT = column[Option[Double]]("le_heading_degT")

  def leDisplacedThresholdFt = column[Option[Double]]("le_displaced_threshold_ft")

  def heIdent = column[Option[String]]("he_ident")

  def heLatitudeDeg = column[Option[Double]]("he_latitude_deg")

  def heLongitudeDeg = column[Option[Double]]("he_longitude_deg")

  def heElevationFt = column[Option[Double]]("he_elevation_ft")

  def heHeadingDegT = column[Option[Double]]("he_heading_degT")

  def heDisplacedThresholdFt = column[Option[Double]]("he_displaced_threshold_ft")

  def airportFk = foreignKey("runway_airport_fk", airportRef, airportsQuery)(_.id)

  override def * : ProvenShape[RunwayRow] = {
    (id,
      (airportRef,
        airportIdent) <> (RunwayAirport.tupled, RunwayAirport.unapply),
      lengthFt,
      widthFt,
      surface,
      lighted,
      closed,
      (leIdent,
        leLatitudeDeg,
        leLongitudeDeg,
        leElevationFt,
        leHeadingDegT,
        leDisplacedThresholdFt) <> (RunwayLe.tupled, RunwayLe.unapply),
      (heIdent,
        heLatitudeDeg,
        heLongitudeDeg,
        heElevationFt,
        heHeadingDegT,
        heDisplacedThresholdFt) <> (RunwayHe.tupled, RunwayHe.unapply)
    ).shaped <> (RunwayRow.tupled, RunwayRow.unapply)

  }
}
