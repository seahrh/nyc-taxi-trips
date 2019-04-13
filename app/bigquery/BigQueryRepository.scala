package bigquery

import javax.inject.{Inject, Singleton}
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class AverageSpeed(date: String, averageSpeed: Double)

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait BigQueryRepository {
  def get(date: String)(implicit mc: MarkerContext): Future[Option[AverageSpeed]]
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

  private val result: List[AverageSpeed] = List(
    AverageSpeed("2019-04-01", 1.1),
    AverageSpeed("2019-04-02", 2.2),
    AverageSpeed("2019-04-03", 3.3),
    AverageSpeed("2019-04-04", 4.4),
    AverageSpeed("2019-04-05", 5.5)
  )

  override def get(date: String)(
    implicit mc: MarkerContext): Future[Option[AverageSpeed]] = {
    Future {
      logger.trace(s"get: date=$date")
      result.find(x => x.date == date)
    }
  }

}
