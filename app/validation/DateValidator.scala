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

final case class DateRangeValidator(
                                     fromDateString: String,
                                     toDateString: String,
                                     formatter: DateTimeFormatter
                                   ) extends Validator {

  override def validate: Set[ValidationError] = {
    val errors: ArrayBuffer[ValidationError] = ArrayBuffer()
    errors ++= DateValidator(fromDateString, formatter).validate
    errors ++= DateValidator(toDateString, formatter).validate
    if (errors.isEmpty) {
      val from: LocalDate = LocalDate.parse(fromDateString, formatter)
      val to: LocalDate = LocalDate.parse(fromDateString, formatter)
      if (from.isAfter(to)) {
        errors += ValidationError(message = s"Invalid date range: from [$fromDateString], to [$toDateString]")
      }
    }
    errors.toSet
  }

}
