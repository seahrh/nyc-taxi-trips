package v1.trip.tripcount

import java.time.LocalDate

import dal.{TripRepository, TripCount}
import javax.inject.Inject
import play.api.MarkerContext
import play.api.libs.json._
import validation.{DateRangeValidator, ValidationError}

import scala.concurrent.{ExecutionContext, Future}

/**
  * DTO for displaying post information.
  */
private[tripcount] final case class Resource(date: String, total_trips: Long)

private[tripcount] object Resource {
  implicit val jsonFormat: Format[Resource] = Json.format[Resource]
}

/**
  * Controls access to the backend data, returning [[Resource]]
  */
private[tripcount] class ResourceHandler @Inject()(
                                                       repo: TripRepository
                                                     )(implicit ec: ExecutionContext) {



  private[tripcount] def lookup(startDate: String, endDate: String)
                                  (implicit mc: MarkerContext): Future[Seq[Resource]] = {
    val start: LocalDate = LocalDate.parse(startDate, repo.dateFormat)
    val end: LocalDate = LocalDate.parse(endDate, repo.dateFormat)
    for (data <- repo.tripCount(start, end)) yield {
      data.map { x => asResource(x) }
    }
  }

  private def asResource(data: TripCount): Resource = {
    Resource(
      date = data.date,
      total_trips = data.count
    )
  }

  private[tripcount] def validate(startDate: String, endDate: String): Option[String] = {
    val errors: Set[ValidationError] = DateRangeValidator(
      fromDateString = startDate,
      toDateString = endDate,
      formatter = repo.dateFormat
    ).validate
    if (errors.isEmpty) {
      None
    } else {
      Option(errors map { e => e.message } mkString ". ")
    }
  }

}
