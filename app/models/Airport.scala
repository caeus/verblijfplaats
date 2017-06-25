package models

import play.api.libs.json.Json

/**
  * Created by caeus on 24/06/17.
  */

case class AirportCodes(gps:Option[String],
                        iata:Option[String],
                        local:Option[String])
object AirportCodes {
  implicit def writes = Json.writes[AirportCodes]
  def tupled = (AirportCodes.apply _).tupled
}
case class AirportLocation(latitude: Double,
                           longitude: Double,
                           elevation: Option[Double])

object AirportLocation {
  implicit def writes = Json.writes[AirportLocation]
  def tupled = (AirportLocation.apply _).tupled
}

case class Airport(id: Long,
                   ident: String,
                   kind: String,
                   name: String,
                   location: AirportLocation,
                   continent: String,
                   isoRegion: String,
                   municipality: Option[String],
                   scheduledService: Boolean,
                   codes: AirportCodes,
                   homeLink: Option[String],
                   wikipediaLink: Option[String],
                   keywords: List[String],
                   runways: Seq[Runway])

object Airport {
  implicit def writes = Json.writes[Airport]
}