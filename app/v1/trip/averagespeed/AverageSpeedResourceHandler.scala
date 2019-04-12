package v1.trip.averagespeed

import java.time.format.DateTimeFormatter

import javax.inject.Inject
import play.api.MarkerContext
import play.api.libs.json._
import validation.{DateValidator, ValidationError}

import scala.concurrent.{ExecutionContext, Future}

/**
  * DTO for displaying post information.
  */
final case class AverageSpeedResource(averageSpeed: Float)

object AverageSpeedResource {

  /**
    * Mapping to write a PostResource out as a JSON value.
    */
  implicit val implicitWrites: Writes[AverageSpeedResource] = (res: AverageSpeedResource) => {
    Json.obj(
      "average_speed" -> res.averageSpeed
    )
  }
}

/**
  * Controls access to the backend data, returning [[AverageSpeedResource]]
  */
class AverageSpeedResourceHandler @Inject()(
                                             repo: AverageSpeedRepository
                                           )(implicit ec: ExecutionContext) {

  private val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE

  def lookup(date: String)(
    implicit mc: MarkerContext): Future[Option[AverageSpeedResource]] = {
    repo.get(date).map {
      case Some(d) => Option(asResource(d))
      case _ => None
    }
  }

  private def asResource(d: AverageSpeedData): AverageSpeedResource = {
    AverageSpeedResource(d.averageSpeed)
  }

  def validate(date: String): Option[String] = {
    val errors: Set[ValidationError] = DateValidator(date, dateFormat).validate
    if (errors.isEmpty) {
      None
    } else {
      Option(errors map { e => e.message } mkString ". ")
    }
  }

}
