package v1.trip.averagespeed

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class AverageSpeedData(id: AverageSpeedId, title: String, body: String)

class AverageSpeedId private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object AverageSpeedId {
  def apply(raw: String): AverageSpeedId = {
    require(raw != null)
    new AverageSpeedId(Integer.parseInt(raw))
  }
}

class AverageSpeedExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait AverageSpeedRepository {
  def get(id: AverageSpeedId)(implicit mc: MarkerContext): Future[Option[AverageSpeedData]]
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

  private val postList = List(
    AverageSpeedData(AverageSpeedId("1"), "title 1", "blog post 1"),
    AverageSpeedData(AverageSpeedId("2"), "title 2", "blog post 2"),
    AverageSpeedData(AverageSpeedId("3"), "title 3", "blog post 3"),
    AverageSpeedData(AverageSpeedId("4"), "title 4", "blog post 4"),
    AverageSpeedData(AverageSpeedId("5"), "title 5", "blog post 5")
  )

  override def get(id: AverageSpeedId)(
      implicit mc: MarkerContext): Future[Option[AverageSpeedData]] = {
    Future {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

}
