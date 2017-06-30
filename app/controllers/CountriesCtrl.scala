package controllers

import javax.inject._

import engines.CountriesEngine
import exceptions.ResourceNotFoundException
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * So I try to make everything restful. It was kind of difficult as
  * Aggregations per se are each a different resource
  * Same with runways, airports and countries.
  * At the end I had to be quite flexible and violate rest principles
  * for the sake of the test
  */
@Singleton
class CountriesCtrl @Inject()(cc: ControllerComponents,
                              countriesEngine: CountriesEngine
                             )(implicit executionContext: ExecutionContext) extends AbstractController(cc) {



  def query(q: String) = Action.async {

    countriesEngine.query(q).map {
      descriptors =>
        Ok(Json.toJson(descriptors))
    }
  }

  def byCode(code: String) = Action.async {
    countriesEngine.byCode(code).flatMap {
      case Some(country) =>
        Future.successful(Ok(Json.toJson(country)))
      case None =>
        Future.failed(ResourceNotFoundException(s"There's no country with code $code"))
    }

  }
}
