package v1.trip.averagespeed

import javax.inject.Inject
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._
import util.Logging
import v1.{RequestMarkerContext, V1ActionBuilder, failurePayload, successPayload}

import scala.concurrent.ExecutionContext

/**
  * Packages up the component dependencies for the post controller.
  *
  * This is a good way to minimize the surface area exposed to the controller, so the
  * controller only has to have one thing injected.
  */
private final case class AverageSpeedControllerComponents @Inject()(
                                                                     action: V1ActionBuilder,
                                                                     resourceHandler: ResourceHandler,
                                                                     actionBuilder: DefaultActionBuilder,
                                                                     parsers: PlayBodyParsers,
                                                                     messagesApi: MessagesApi,
                                                                     langs: Langs,
                                                                     fileMimeTypes: FileMimeTypes,
                                                                     executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents


/**
  * Takes HTTP requests and produces JSON.
  */
final class AverageSpeedController @Inject()(cc: AverageSpeedControllerComponents)(
  implicit ec: ExecutionContext)
  extends BaseController
    with Logging
    with RequestMarkerContext {

  override protected def controllerComponents: ControllerComponents = cc

  private def action: V1ActionBuilder = cc.action

  private def resourceHandler: ResourceHandler = cc.resourceHandler

  def show(date: String): Action[AnyContent] = {
    resourceHandler.validate(date) match {
      case Some(err) => Action(BadRequest(failurePayload(err)))
      case _ =>
        action.async {
          implicit request =>
            log.trace(s"show: date = $date")
            resourceHandler.lookup(date).map {
              case Some(data) => Ok(successPayload[Resource](Seq(data)))
              case _ =>
                NotFound(failurePayload(s"Average speed not found for date: $date"))
            }
        }
    }
  }

}
