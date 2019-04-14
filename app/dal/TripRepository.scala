package dal

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import javax.inject.{Inject, Singleton}
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class AverageSpeed(date: String, averageSpeed: Float)

final case class AverageFareByPickupLocation(date: String, lat: Float, lng: Float, fare: Float)

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
