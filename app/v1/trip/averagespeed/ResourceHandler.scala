package v1.trip.averagespeed

import java.time.LocalDate

import dal.{AverageSpeed, TripRepository}
import javax.inject.Inject
import play.api.{Configuration, MarkerContext}
import play.api.libs.json._
import v1.roundUp
import validation.{DateValidator, ValidationError}

import scala.concurrent.{ExecutionContext, Future}

/**
  * DTO for displaying resource information.
  */
private[averagespeed] final case class Resource(average_speed: BigDecimal)

private[averagespeed] object Resource {
  implicit val jsonFormat: Format[Resource] = Json.format[Resource]
}

/**
  * Controls access to the backend data, returning [[Resource]]
  */
private[averagespeed] class ResourceHandler @Inject()(
                                                       repo: TripRepository,
                                                       conf: Configuration
                                                     )(implicit ec: ExecutionContext) {

  private val DECIMAL_PLACES: Int = conf.get[Int]("v1.trip.averagespeed.ResourceHandler.DECIMAL_PLACES")

  /**
    * Retrieves data from the repository and converts it to the Resource object.
    *
    * @param date date in YYYY-MM-DD format
    * @param mc   MarkerContext
    * @return Future containing the result
    */
  private[averagespeed] def lookup(date: String)
                                  (implicit mc: MarkerContext): Future[Option[Resource]] = {
    val d: LocalDate = LocalDate.parse(date, repo.dateFormat)
    repo.avgSpeed(d).map {
      case Some(data) => Option(asResource(data))
      case _ => None
    }
  }

  private def asResource(data: AverageSpeed): Resource = {
    Resource(roundUp(data.speed, decimalPlaces = DECIMAL_PLACES))
  }

  /**
    * Validates input date
    *
    * @param date parameter in resource URI
    * @return error message, if any
    */
  private[averagespeed] def validate(date: String): Option[String] = {
    val errors: Set[ValidationError] = DateValidator(date, repo.dateFormat).validate
    if (errors.isEmpty) {
      None
    } else {
      Option(errors map { e => e.message } mkString ". ")
    }
  }

}
