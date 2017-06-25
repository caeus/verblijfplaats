package config

import com.google.inject.{Inject, Singleton}
import daos.{AirportsDao, CountriesDao, RunwaysDao}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import sql.queries.{AirportsQuery, CountriesQuery, RunwaysQuery}

import scala.concurrent.ExecutionContext

/**
  * Created by caeus on 24/06/17.
  */

class VerblijfplaatsModule extends Module {
  override def bindings(environment: Environment,
                        configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[Init].toSelf.eagerly())
  }
}

@Singleton
class Init @Inject()(countriesDao: CountriesDao,
                     airportsDao: AirportsDao,
                     runwaysDao: RunwaysDao,
                     countriesQuery: CountriesQuery,
                     airportsQuery: AirportsQuery,
                     runwaysQuery: RunwaysQuery)(implicit executionContext: ExecutionContext) {
  //I had all this code to populate the database mainly
  // Also to define the ddl with the help of slick
  //import sql.drivers.ExtendedPGProfile.api._

  //private val ddl = countriesQuery.schema ++ airportsQuery.schema ++ runwaysQuery.schema

  //println(ddl.createStatements.mkString(";\n") + ";")
  //println("# --- !Downs")
  //println(ddl.dropStatements.mkString(";\n") + ";")

  //private val countriesInit = countriesDao.init

  //  private val airportsInit = countriesInit
  //    .flatMap(_ => airportsDao.init)
  //  private val runwaysInit = airportsInit
  //    .flatMap(_ => runwaysDao.init)
  //
  //  countriesInit.onComplete(println)
  //
  //  airportsInit.onComplete(println)
  //
  //  runwaysInit.onComplete(println)
}
