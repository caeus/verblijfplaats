package sql.queries


import com.google.inject.{Inject, Singleton}
import controllers.Assets.Asset
import slick.lifted.ProvenShape
import sql.drivers.ExtendedPGProfile.api._

//I like creating queries as injectables, so that I can use them anywhere in the code
//Although ideally they will only be used in the dao layer
@Singleton
class CountriesQuery @Inject()() extends TableQuery(new CountrySchema(_))

/**
  * Created by caeus on 24/06/17.
  */
case class CountryRow(id: Long,
                   code: String,
                   name: String,
                   continent: String,
                   wikipediaLink: String,
                   keywords: List[String])

class CountrySchema(tag: Tag) extends Table[CountryRow](tag, "country") {
  def id = column[Long]("id")

  def code = column[String]("code",O.PrimaryKey)

  def name = column[String]("name")

  def continent = column[String]("continent")

  def wikipediaLink = column[String]("wikipedia_link")

  def keywords = column[List[String]]("keywords")
  override def * : ProvenShape[CountryRow] = (
    id,
    code,
    name,
    continent,
    wikipediaLink,
    keywords
  ) <> (CountryRow.tupled, CountryRow.unapply)
}
