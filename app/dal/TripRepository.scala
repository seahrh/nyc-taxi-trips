package dal

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.MarkerContext
import play.api.libs.json.{Format, Json}

import scala.concurrent.Future

/**
  * Average speed of trips in a given day
  *
  * @param date date in YYYY-MM-DD format
  * @param speed daily average speed
  */
final case class AverageSpeed(date: String, speed: Float)

private[dal] object AverageSpeed {
  implicit val jsonFormat: Format[AverageSpeed] = Json.format[AverageSpeed]
}

/**
  * Average fare by pickup location (represented as a S2 cell)
  *
  * @param lat pickup latitude
  * @param lng pickup longitude
  * @param fare fare amount
  */
final case class AverageFareByPickupLocation(lat: Float, lng: Float, fare: Float)

private[dal] object AverageFareByPickupLocation {
  implicit val jsonFormat: Format[AverageFareByPickupLocation] = Json.format[AverageFareByPickupLocation]
}

/**
  * Trip count of a given day
  *
  * @param date date in YYYY-MM-DD format
  * @param count trip count
  */
final case class TripCount(date: String, count: Long)

private[dal] object TripCount {
  implicit val jsonFormat: Format[TripCount] = Json.format[TripCount]
}

/**
  * A pure non-blocking interface for the Trip Repository.
  */
trait TripRepository {
  val dateFormat: DateTimeFormatter

  /**
    * Returns the average speed of trips in a given day
    *
    * @param date date
    * @param mc MarkerContext
    * @return Future containing the result
    */
  def avgSpeed(date: LocalDate)
              (implicit mc: MarkerContext): Future[Option[AverageSpeed]]

  /**
    * Returns a list of average fares grouped by pickup location (S2 cells) in a given day
    *
    * @param date date
    * @param mc MarkerContext
    * @return Future containing the result
    */
  def avgFareByPickupLocation(date: LocalDate)
                             (implicit mc: MarkerContext): Future[Seq[AverageFareByPickupLocation]]

  /**
    * Returns a list of daily trip counts in the given date range.
    *
    * @param from first date inclusive
    * @param to last date inclusive
    * @param mc MarkerContext
    * @return Future containing the result
    */
  def tripCount(from: LocalDate, to: LocalDate)
               (implicit mc: MarkerContext): Future[Seq[TripCount]]
}
