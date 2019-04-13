package v1.trip.averagespeed

import java.time.format.DateTimeFormatter

import bigquery.{AverageSpeed, BigQueryRepository}
import javax.inject.Inject
import play.api.MarkerContext
import play.api.libs.json._
import validation.{DateValidator, ValidationError}

import scala.concurrent.{ExecutionContext, Future}
import v1.roundUp

/**
  * DTO for displaying post information.
  */
private[averagespeed] final case class Resource(average_speed: Double)

private[averagespeed] object Resource {
  implicit val jsonFormat: Format[Resource] = Json.format[Resource]
}

/**
  * Controls access to the backend data, returning [[Resource]]
  */
private[averagespeed] class ResourceHandler @Inject()(
                                                       repo: BigQueryRepository
                                                     )(implicit ec: ExecutionContext) {

  private val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE

  private[averagespeed] def lookup(date: String)(
    implicit mc: MarkerContext): Future[Option[Resource]] = {
    repo.get(date).map {
      case Some(d) => Option(asResource(d))
      case _ => None
    }
  }

  private def asResource(d: AverageSpeed): Resource = {
    Resource(roundUp(d.averageSpeed, decimalPlaces = 1))
  }

  private[averagespeed] def validate(date: String): Option[String] = {
    val errors: Set[ValidationError] = DateValidator(date, dateFormat).validate
    if (errors.isEmpty) {
      None
    } else {
      Option(errors map { e => e.message } mkString ". ")
    }
  }

}
