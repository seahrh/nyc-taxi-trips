package bigquery

import java.time.LocalDate

import javax.inject.{Inject, Singleton}
import play.api.{Logger, MarkerContext}


import scala.concurrent.Future

final case class AverageSpeed(date: String, averageSpeed: Float)

final case class AverageFareByPickupLocation(date: String, s2id: String, fare: Float)

final case class TripCount(date: String, count: Long)

/**
  * A pure non-blocking interface for the PostRepository.
  */
sealed trait BigQueryRepository {
  def avgSpeed(date: String)
              (implicit mc: MarkerContext): Future[Option[AverageSpeed]]

  def avgFareByPickupLocation(date: LocalDate)
                             (implicit mc: MarkerContext): Future[Option[Seq[AverageFareByPickupLocation]]]

  def tripCount(from: LocalDate, to: LocalDate)
               (implicit mc: MarkerContext): Future[Option[Seq[TripCount]]]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
final class BigQueryRepositoryImpl @Inject()()(implicit ec: RepositoryExecutionContext)
  extends BigQueryRepository {

  private val logger = Logger(this.getClass)

  private val result: List[AverageSpeed] = List(
    AverageSpeed("2019-04-01", 1.1F),
    AverageSpeed("2019-04-02", 2.2F),
    AverageSpeed("2019-04-03", 3.3F),
    AverageSpeed("2019-04-04", 4.4F),
    AverageSpeed("2019-04-05", 5.5F)
  )

  override def avgSpeed(date: String)(
    implicit mc: MarkerContext): Future[Option[AverageSpeed]] = {
    Future {
      logger.trace(s"averageSpeed: date=$date")
      result.find(x => x.date == date)
    }
  }

  override def avgFareByPickupLocation(date: LocalDate)
                                      (implicit mc: MarkerContext): Future[Option[Seq[AverageFareByPickupLocation]]] = {
    throw new NotImplementedError
  }

  override def tripCount(from: LocalDate, to: LocalDate)
                        (implicit mc: MarkerContext): Future[Option[Seq[TripCount]]] = {
    throw new NotImplementedError
  }
}
