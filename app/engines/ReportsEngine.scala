package engines

import com.google.inject.{Inject, Singleton}
import daos.CountriesDao
import models.{CountriesAirportCountEdges, CountryRunwayHistogram, RunwayMode}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by caeus on 25/06/17.
  */
@Singleton
class ReportsEngine @Inject()(dao: CountriesDao)(implicit executionContext: ExecutionContext) {
  def runwayModes: Future[Seq[RunwayMode]] ={
    dao.runwayModes
  }


  def airportCountEdges: Future[CountriesAirportCountEdges] = {
    val eventualTops = dao.airportCounts(10, asc = false)
    val eventualBottoms = dao.airportCounts(10, asc = true)
    for {
      tops <- eventualTops
      bottoms <- eventualBottoms
    } yield {
      CountriesAirportCountEdges(top = tops, bottom = bottoms)
    }
  }

  def runwayHistograms: Future[Seq[CountryRunwayHistogram]] = {
    dao.runwayHistograms
  }

}
