package v1.trip.averagespeed

import javax.inject.{Inject, Provider}
import play.api.MarkerContext
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * DTO for displaying post information.
  */
final case class AverageSpeedResource(id: String, title: String, body: String)

object AverageSpeedResource {

  /**
    * Mapping to write a PostResource out as a JSON value.
    */
  implicit val implicitWrites: Writes[AverageSpeedResource] = new Writes[AverageSpeedResource] {
    def writes(post: AverageSpeedResource): JsValue = {
      Json.obj(
        "id" -> post.id,
        "title" -> post.title,
        "body" -> post.body
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[AverageSpeedResource]]
  */
class AverageSpeedResourceHandler @Inject()(
                                             repo: AverageSpeedRepository
                                           )(implicit ec: ExecutionContext) {

  def lookup(id: String)(
      implicit mc: MarkerContext): Future[Option[AverageSpeedResource]] = {
    val postFuture = repo.get(AverageSpeedId(id))
    postFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createResource(postData)
      }
    }
  }

  private def createResource(p: AverageSpeedData): AverageSpeedResource = {
    AverageSpeedResource(p.id.toString, p.title, p.body)
  }

}
