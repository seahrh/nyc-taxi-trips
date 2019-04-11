import play.api.libs.json.{JsObject, Json, Writes}


package object v1 {

  final case class Error(message: String)

  object Error {

    /**
      * Mapping to write a PostResource out as a JSON value.
      */
    implicit val implicitWrites: Writes[Error] = (e: Error) => {
      Json.obj(
        "message" -> e.message
      )
    }
  }

  private[v1] def successPayload[T](data: Seq[T])(implicit tjs: Writes[T]): JsObject = {
    Json.obj("data" -> Json.toJson(data))
  }

  private[v1] def failurePayload(message: String): JsObject = {
    Json.obj("error" -> Json.toJson(Error(message)))
  }

}
