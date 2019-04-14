package v1.trip.tripcount

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
private final case class TripCountControllerComponents @Inject()(
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
final class TripCountController @Inject()(cc: TripCountControllerComponents)(
  implicit ec: ExecutionContext)
  extends BaseController
    with Logging
    with RequestMarkerContext {

  override protected def controllerComponents: ControllerComponents = cc

  private def action: V1ActionBuilder = cc.action

  private def resourceHandler: ResourceHandler = cc.resourceHandler

  def show(start: String, end: String): Action[AnyContent] = {
    resourceHandler.validate(start, end) match {
      case Some(err) => Action(BadRequest(failurePayload(err)))
      case _ =>
        action.async {
          implicit request =>
            log.trace(s"show: start=$start, end=$end")
            for (data <- resourceHandler.lookup(start, end)) yield {
              if (data.isEmpty) {
                NotFound(failurePayload(s"Total trips not found for date range: $start to $end"))
              } else {
                Ok(successPayload[Resource](data))
              }
            }
        }
    }
  }

}
