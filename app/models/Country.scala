package models

import play.api.libs.json.Json

/**
  * Created by caeus on 24/06/17.
  */


case class Country(id: Long,
                   code: String,
                   name: String,
                   continent: String,
                   wikipediaLink: String,
                   keywords: List[String],
                   airports:Seq[Airport])

object Country{
  implicit def writes=Json.writes[Country]
}
case class CountryDescriptor(
                              id: Long,
                              code: String,
                              name: String,
                              continent: String,
                              wikipediaLink: String,
                              keywords: List[String]
                            )

object CountryDescriptor{
  implicit def writes=Json.writes[CountryDescriptor]
}


