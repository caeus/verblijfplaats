package controllers

import javax.inject._

import engines.ReportsEngine
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ReportsCtrl @Inject()(cc: ControllerComponents,
                            reportsEngine: ReportsEngine
                           )(implicit executionContext: ExecutionContext) extends AbstractController(cc) {


  def airportCountEdges = Action.async {

      reportsEngine.airportCountEdges.map {
        report => Ok(Json.toJson(report))
      }
  }

  def runwayHistograms = Action.async {

      reportsEngine.runwayHistograms.map {
        report => Ok(Json.toJson(report))
      }
  }
  def runwayModes = Action.async {

    reportsEngine.runwayModes.map {
      report => Ok(Json.toJson(report))
    }
  }
}
