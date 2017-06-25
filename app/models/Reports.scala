package models

import play.api.libs.json.Json

/**
  * Created by caeus on 25/06/17.
  */

case class CountryAirportCount(id: Long,
                               code: String,
                               name: String,
                               airportCount:Int)
object CountryAirportCount{
  implicit def writes=Json.writes[CountryAirportCount]
}
case class CountriesAirportCountEdges(
                                       top:Seq[CountryAirportCount],
                                       bottom:Seq[CountryAirportCount]
                                       )

object CountriesAirportCountEdges{
  implicit def writes=Json.writes[CountriesAirportCountEdges]
}


case class CountryRunwayHistogram(id: Long,
                                  code: String,
                                  name: String,
                                  data:Map[String,Int])
object CountryRunwayHistogram{
  implicit def writes=Json.writes[CountryRunwayHistogram]
}
case class RunwayMode(ident:String,count:Int)

object RunwayMode{
  implicit def writes=Json.writes[RunwayMode]
}


