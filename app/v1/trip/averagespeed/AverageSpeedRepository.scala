package v1.trip.averagespeed

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class AverageSpeedData(date: String, averageSpeed: Double)

class AverageSpeedExecutionContext @Inject()(actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait AverageSpeedRepository {
  def get(date: String)(implicit mc: MarkerContext): Future[Option[AverageSpeedData]]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class AverageSpeedRepositoryImpl @Inject()()(implicit ec: AverageSpeedExecutionContext)
  extends AverageSpeedRepository {

  private val logger = Logger(this.getClass)

  private val result: List[AverageSpeedData] = List(
    AverageSpeedData("2019-04-01", 1.1F),
    AverageSpeedData("2019-04-02", 2.2F),
    AverageSpeedData("2019-04-03", 3.3F),
    AverageSpeedData("2019-04-04", 4.4F),
    AverageSpeedData("2019-04-05", 5.5F)
  )

  override def get(date: String)(
    implicit mc: MarkerContext): Future[Option[AverageSpeedData]] = {
    Future {
      logger.trace(s"get: date=$date")
      result.find(x => x.date == date)
    }
  }

}
