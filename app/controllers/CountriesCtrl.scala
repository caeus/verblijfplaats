package controllers

import javax.inject._

import engines.CountriesEngine
import exceptions.ResourceNotFoundException
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class CountriesCtrl @Inject()(cc: ControllerComponents,
                              countriesEngine: CountriesEngine
                             )(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

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
