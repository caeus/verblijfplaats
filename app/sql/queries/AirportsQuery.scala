package sql.queries

import com.google.inject.{Inject, Singleton}
import models.{AirportCodes, AirportLocation}
import slick.lifted.ProvenShape
import sql.drivers.ExtendedPGProfile.api._

@Singleton
class AirportsQuery @Inject()(countriesQuery: CountriesQuery) extends TableQuery(new AirportSchema(_, countriesQuery))

/**
  * Created by caeus on 24/06/17.
  */
case class AirportRow(id: Long,
                      ident: String,
                      kind: String,
                      name: String,
                      location: AirportLocation,
                      continent: String,
                      isoCountry: String,
                      isoRegion: String,
                      municipality: Option[String],
                      scheduledService: Boolean,
                      codes: AirportCodes,
                      homeLink: Option[String],
                      wikipediaLink: Option[String],
                      keywords: List[String])


class AirportSchema(tag: Tag, countriesQuery: CountriesQuery) extends Table[AirportRow](tag, "airport") {

  def id = column[Long]("id", O.PrimaryKey)

  def ident = column[String]("ident")

  def kind = column[String]("kind")

  def name = column[String]("name")

  def latitudeDeg = column[Double]("latitude_deg")

  def longitudeDeg = column[Double]("longitude_deg")

  def elevationFt = column[Option[Double]]("elevation_ft")

  def continent = column[String]("continent")

  def isoCountry = column[String]("iso_country")

  def isoRegion = column[String]("iso_region")

  def municipality = column[Option[String]]("municipality")

  def scheduledService = column[Boolean]("scheduled_service")

  def gpsCode = column[Option[String]]("gps_code")

  def iataCode = column[Option[String]]("iata_code")

  def localCode = column[Option[String]]("local_code")

  def homeLink = column[Option[String]]("home_link")

  def wikipediaLink = column[Option[String]]("wikipedia_link")

  def keywords = column[List[String]]("keywords")

  def countryFk = foreignKey("airport_country_fk", isoCountry, countriesQuery)(_.code)

  override def * : ProvenShape[AirportRow] = {
    (id,
      ident,
      kind,
      name,
      (latitudeDeg,
        longitudeDeg,
        elevationFt) <> (AirportLocation.tupled, AirportLocation.unapply),
      continent,
      isoCountry,
      isoRegion,
      municipality,
      scheduledService,
      (gpsCode,
        iataCode,
        localCode) <> (AirportCodes.tupled, AirportCodes.unapply),
      homeLink,
      wikipediaLink,
      keywords) <> (AirportRow.tupled, AirportRow.unapply)
  }
}
