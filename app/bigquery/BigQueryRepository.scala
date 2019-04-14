package bigquery

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
trait BigQueryRepository {
  val dateFormat: DateTimeFormatter

  def avgSpeed(date: LocalDate)
              (implicit mc: MarkerContext): Future[Option[AverageSpeed]]

  def avgFareByPickupLocation(date: LocalDate)
                             (implicit mc: MarkerContext): Future[Seq[AverageFareByPickupLocation]]

  def tripCount(from: LocalDate, to: LocalDate)
               (implicit mc: MarkerContext): Future[Seq[TripCount]]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class BigQueryRepositoryImpl @Inject()()(implicit ec: RepositoryExecutionContext)
  extends BigQueryRepository {

  private val logger = Logger(this.getClass)

  override val dateFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  override def avgSpeed(date: LocalDate)(
    implicit mc: MarkerContext): Future[Option[AverageSpeed]] = {
    Future {
      logger.trace(s"avgSpeed: date=$date")
      val ds: String = date.format(dateFormat)
      val result: Seq[AverageSpeed] = Seq(
        AverageSpeed("2019-04-01", 1.1F),
        AverageSpeed("2019-04-02", 2.2F),
        AverageSpeed("2019-04-03", 3.3F),
        AverageSpeed("2019-04-04", 4.4F),
        AverageSpeed("2019-04-05", 5.5F)
      )
      result.find(x => x.date == ds)
    }
  }

  override def avgFareByPickupLocation(date: LocalDate)
                                      (implicit mc: MarkerContext): Future[Seq[AverageFareByPickupLocation]] = {
    Future {
      logger.trace(s"avgFareByPickupLocation: date=$date")
      val ds: String = date.format(dateFormat)
      val result: Seq[AverageFareByPickupLocation] = Seq(
        AverageFareByPickupLocation("2019-04-01", 1.276162F, 103.847333F, 1.11F),
        AverageFareByPickupLocation("2019-04-01", 1.276162F, 103.847333F, 2.22F),
        AverageFareByPickupLocation("2019-04-01", 1.276162F, 103.847333F, 3.33F),
        AverageFareByPickupLocation("2019-04-02", 1.276162F, 103.847333F, 4.44F),
        AverageFareByPickupLocation("2019-04-02", 1.276162F, 103.847333F, 5.55F)
      )
      result.filter(x => x.date == ds)
    }
  }

  override def tripCount(from: LocalDate, to: LocalDate)
                        (implicit mc: MarkerContext): Future[Seq[TripCount]] = {
    Future {
      logger.trace(s"tripCount: from=$from, to=$to")
      val froms: String = from.format(dateFormat)
      val tos: String = to.format(dateFormat)
      val result: Seq[TripCount] = Seq(
        TripCount("2019-04-01", 111), //scalastyle:ignore
        TripCount("2019-04-02", 222), //scalastyle:ignore
        TripCount("2019-04-03", 333), //scalastyle:ignore
        TripCount("2019-04-04", 444), //scalastyle:ignore
        TripCount("2019-04-05", 555) //scalastyle:ignore
      )
      result.filter(x => x.date >= froms && x.date <= tos)
    }
  }
}
