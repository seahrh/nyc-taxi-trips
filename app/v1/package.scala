import play.api.libs.json.{Format, JsValue, Json, Writes}


package object v1 {

  final case class Error(message: String)

  object Error {
    implicit val jsonFormat: Format[Error] = Json.format[Error]
  }

  final case class SuccessPayload(data: JsValue)

  object SuccessPayload {
    implicit val jsonFormat: Format[SuccessPayload] = Json.format[SuccessPayload]
  }

  private[v1] def successPayload[T](data: Seq[T])(implicit tjs: Writes[T]): JsValue = {
    Json.toJson(SuccessPayload(data = Json.toJson(data)))
  }

  final case class FailurePayload(error: Error)

  object FailurePayload {
    implicit val jsonFormat: Format[FailurePayload] = Json.format[FailurePayload]
  }

  private[v1] def failurePayload(message: String): JsValue = {
    Json.toJson(FailurePayload(error = Error(message)))
  }

  private[v1] def roundUp(d: Double, decimalPlaces: Int): BigDecimal = {
    BigDecimal(d).setScale(decimalPlaces, BigDecimal.RoundingMode.HALF_UP)
  }

  private[v1] def roundUp(f: Float, decimalPlaces: Int): BigDecimal = {
    roundUp(f.toDouble, decimalPlaces)
  }

}
