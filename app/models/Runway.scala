package models

import play.api.libs.json.Json

/**
  * Created by caeus on 24/06/17.
  */


case class RunwayLe(ident: Option[String],
                    latitude: Option[Double],
                    longitude: Option[Double],
                    elevation: Option[Double],
                    headingT: Option[Double],
                    displacedThreshold: Option[Double])
object RunwayLe{
  implicit def writes=Json.writes[RunwayLe]
  def tupled = (RunwayLe.apply _).tupled
}
case class RunwayHe(ident: Option[String],
                    latitude: Option[Double],
                    longitude: Option[Double],
                    elevation: Option[Double],
                    headingT: Option[Double],
                    displacedThreshold: Option[Double])

object RunwayHe{
  implicit def writes=Json.writes[RunwayHe]
  def tupled = (RunwayHe.apply _).tupled
}

case class RunwayAirport(ref: Long, ident: String)


case class Runway(id: Long,
                  lengthFt: Option[Double],
                  widthFt: Option[Double],
                  surface: Option[String],
                  lighted: Double,
                  closed: Double,
                  le: RunwayLe,
                  he: RunwayHe)
object Runway{
  implicit def writes=Json.writes[Runway]
}