package v1.trip.averagefareheatmap

import javax.inject.Inject
import play.api.Logger
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._
import v1.{RequestMarkerContext, V1ActionBuilder, failurePayload, successPayload}

import scala.concurrent.ExecutionContext

/**
  * Packages up the component dependencies for the post controller.
  *
  * This is a good way to minimize the surface area exposed to the controller, so the
  * controller only has to have one thing injected.
  */
private final case class AverageFareHeatmapControllerComponents @Inject()(
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
final class AverageFareHeatmapController @Inject()(cc: AverageFareHeatmapControllerComponents)(
  implicit ec: ExecutionContext)
  extends BaseController
    with RequestMarkerContext {

  private val logger = Logger(getClass)

  override protected def controllerComponents: ControllerComponents = cc

  private def action: V1ActionBuilder = cc.action

  private def resourceHandler: ResourceHandler = cc.resourceHandler

  def show(date: String): Action[AnyContent] = {
    resourceHandler.validate(date) match {
      case Some(err) => Action(BadRequest(failurePayload(err)))
      case _ =>
        action.async {
          implicit request =>
            logger.trace(s"show: date = $date")
            for (data <- resourceHandler.lookup(date)) yield {
              if (data.isEmpty) {
                NotFound(failurePayload(s"Average speed not found for date: $date"))
              } else {
                Ok(successPayload[Resource](data))
              }
            }
        }
    }
  }

}
