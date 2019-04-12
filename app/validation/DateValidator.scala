package validation

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

import scala.collection.mutable.ArrayBuffer


final case class DateValidator(dateString: String, formatter: DateTimeFormatter) extends Validator {

  override def validate: Set[ValidationError] = {
    val errors: ArrayBuffer[ValidationError] = ArrayBuffer()
    try {
      LocalDate.parse(dateString, formatter)
    } catch {
      case _: DateTimeParseException =>
        errors += ValidationError(message = s"Invalid date [$dateString]")
    }
    errors.toSet
  }

}
