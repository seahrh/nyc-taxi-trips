package v1.trip.averagespeed

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import v1.{failurePayload, successPayload}

import scala.concurrent.ExecutionContext

/**
  * Takes HTTP requests and produces JSON.
  */
class AverageSpeedController @Inject()(cc: AverageSpeedControllerComponents)(
    implicit ec: ExecutionContext)
    extends AverageSpeedBaseController(cc) {

  private val logger = Logger(getClass)

  def show(date: String): Action[AnyContent] = action.async {
    implicit request =>
      logger.trace(s"show: date = $date")
      resourceHandler.lookup(date).map {
        case Some(data) => Ok(successPayload[AverageSpeedResource](Seq(data)))
        case _ =>
          NotFound(failurePayload(s"Average speed not found for date: $date"))
      }
  }


}
