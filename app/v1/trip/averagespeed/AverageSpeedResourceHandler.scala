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
final case class AverageSpeedResource(average_speed: Float)

object AverageSpeedResource {
  implicit val jsonFormat: Format[AverageSpeedResource] = Json.format[AverageSpeedResource]
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
