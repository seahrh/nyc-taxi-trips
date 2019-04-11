package v1.trip.averagespeed

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

final case class PostFormInput(title: String, body: String)

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
      resourceHandler.lookup(date).map { post =>
        Ok(Json.toJson(post))
      }
  }
}
