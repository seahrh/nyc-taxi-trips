package v1.trip.averagefareheatmap

import java.time.LocalDate

import dal.TripRepository
import geom.s2id
import javax.inject.Inject
import play.api.{Configuration, MarkerContext}
import play.api.libs.json._
import v1.roundUp
import validation.{DateValidator, ValidationError}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

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
                                                       repo: TripRepository,
                                                       conf: Configuration
                                                     )(implicit ec: ExecutionContext) {

  private val S2_CELL_LEVEL: Int = conf.get[Int]("v1.trip.averagefareheatmap.ResourceHandler.S2_CELL_LEVEL")

  private val DECIMAL_PLACES: Int = conf.get[Int]("v1.trip.averagefareheatmap.ResourceHandler.DECIMAL_PLACES")

  private[averagefareheatmap] def lookup(date: String)
                                  (implicit mc: MarkerContext): Future[Seq[Resource]] = {
    val d: LocalDate = LocalDate.parse(date, repo.dateFormat)
    for (data <- repo.avgFareByPickupLocation(d)) yield {
      val res: mutable.Map[String, Float] = mutable.Map()
      for (d <- data) {
        val k: String = s2id(latitude = d.lat, longitude = d.lng, level = S2_CELL_LEVEL)
        res.get(k) match {
          case Some(fare) => res += k -> avg(fare, d.fare)
          case _ => res += k -> d.fare
        }
      }
      for ((s2id, fare) <- res.toSeq) yield {
        Resource(
          s2id = s2id,
          fare = roundUp(fare, decimalPlaces = DECIMAL_PLACES)
        )
      }
    }
  }

  private def avg(v1: Float, v2: Float): Float = {
    (v1 / 2) + (v2 / 2)
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
