package dal

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.MarkerContext

import scala.concurrent.Future

final case class AverageSpeed(date: String, speed: Float)

final case class AverageFareByPickupLocation(lat: Float, lng: Float, fare: Float)

final case class TripCount(date: String, count: Long)

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait TripRepository {
  val dateFormat: DateTimeFormatter

  def avgSpeed(date: LocalDate)
              (implicit mc: MarkerContext): Future[Option[AverageSpeed]]

  def avgFareByPickupLocation(date: LocalDate)
                             (implicit mc: MarkerContext): Future[Seq[AverageFareByPickupLocation]]

  def tripCount(from: LocalDate, to: LocalDate)
               (implicit mc: MarkerContext): Future[Seq[TripCount]]
}
