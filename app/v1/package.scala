import play.api.libs.json.{Format, JsValue, Json, Writes}


package object v1 {

  /**
    * Error Json object
    *
    * @param message error message
    */
  final case class Error(message: String)

  object Error {
    implicit val jsonFormat: Format[Error] = Json.format[Error]
  }

  /**
    * Json object containing data in a successful api call.
    *
    * @param data Array of requested data objects
    */
  final case class SuccessPayload(data: JsValue)

  object SuccessPayload {
    implicit val jsonFormat: Format[SuccessPayload] = Json.format[SuccessPayload]
  }

  /**
    * Converts a list of resource objects to the Json representation of a [[SuccessPayload]]
    *
    * @param data list of resource objects
    * @param tjs  implicit json-writer for type T
    * @tparam T given type of resource object
    * @return Json representation of a [[SuccessPayload]]
    */
  private[v1] def successPayload[T](data: Seq[T])(implicit tjs: Writes[T]): JsValue = {
    Json.toJson(SuccessPayload(data = Json.toJson(data)))
  }

  /**
    * Failure payload that is returned by a failed api call.
    *
    * @param error Error object
    */
  final case class FailurePayload(error: Error)

  object FailurePayload {
    implicit val jsonFormat: Format[FailurePayload] = Json.format[FailurePayload]
  }

  /**
    * Converts a error message to the Json representation of a [[FailurePayload]]
    *
    * @param message error message
    * @return Json representation of a [[FailurePayload]]
    */
  private[v1] def failurePayload(message: String): JsValue = {
    Json.toJson(FailurePayload(error = Error(message)))
  }

  /**
    * Rounds up a Double value to the required scale.
    *
    * @param d             Double value
    * @param decimalPlaces number of decimal places
    * @return rounded value
    */
  private[v1] def roundUp(d: Double, decimalPlaces: Int): BigDecimal = {
    BigDecimal(d).setScale(decimalPlaces, BigDecimal.RoundingMode.HALF_UP)
  }

  /**
    * Rounds up a Float value to the required scale.
    *
    * @param f             Float value
    * @param decimalPlaces number of decimal places
    * @return rounded value
    */
  private[v1] def roundUp(f: Float, decimalPlaces: Int): BigDecimal = {
    roundUp(f.toDouble, decimalPlaces)
  }

}
