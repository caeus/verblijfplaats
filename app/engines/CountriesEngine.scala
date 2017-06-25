package engines

import com.google.inject.{Inject, Singleton}
import daos.CountriesDao
import models.{Country, CountryDescriptor}

import scala.concurrent.Future

/**
  * Created by caeus on 24/06/17.
  *
  * I like having one "engine" or "service" per resource
  * It should contain business logic...
  * There wasn't much business logic as you can see
  */
@Singleton
class CountriesEngine @Inject()(dao: CountriesDao) {


  def query(q:String):Future[Seq[CountryDescriptor]]={
    dao.query(q)
  }

  def byCode(code:String):Future[Option[Country]]={
    dao.byCode(code)
  }


}
