package v1.trip.averagefareheatmap

import java.time.LocalDate

import dal.{AverageFareByPickupLocation, BigQueryRepository}
import javax.inject.Inject
import play.api.MarkerContext
import play.api.libs.json._
import v1.roundUp
import validation.{DateValidator, ValidationError}

import scala.concurrent.{ExecutionContext, Future}
import geom.s2id

/**
  * DTO for displaying post information.
  */
private[averagefareheatmap] final case class Resource(s2id: String, fare: BigDecimal)

private[averagefareheatmap] object Resource {
  implicit val jsonFormat: Format[Resource] = Json.format[Resource]
}

/**
  * Controls access to the backend data, returning [[Resource]]
  */
private[averagefareheatmap] class ResourceHandler @Inject()(
                                                       repo: BigQueryRepository
                                                     )(implicit ec: ExecutionContext) {

  private val S2_CELL_LEVEL: Int = 16

  private[averagefareheatmap] def lookup(date: String)
                                  (implicit mc: MarkerContext): Future[Seq[Resource]] = {
    val d: LocalDate = LocalDate.parse(date, repo.dateFormat)
    for (data <- repo.avgFareByPickupLocation(d)) yield {
      data.map { x => asResource(x) }
    }
  }

  private def asResource(data: AverageFareByPickupLocation): Resource = {
    Resource(
      s2id = s2id(latitude = data.lat, longitude = data.lng, level = S2_CELL_LEVEL),
      fare = roundUp(data.fare, decimalPlaces = 2)
    )
  }

  private[averagefareheatmap] def validate(date: String): Option[String] = {
    val errors: Set[ValidationError] = DateValidator(date, repo.dateFormat).validate
    if (errors.isEmpty) {
      None
    } else {
      Option(errors map { e => e.message } mkString ". ")
    }
  }

}
