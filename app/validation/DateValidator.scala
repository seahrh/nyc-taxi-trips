package validation

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

import scala.collection.mutable.ArrayBuffer

/**
  * Checks if a given string can be parsed to a date.
  *
  * @param dateString input string
  * @param formatter  given date format
  */
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

/**
  * Checks if a given pair of strings can be parsed to a valid date range.
  *
  * @param fromDateString first date in the range, inclusive
  * @param toDateString   last date in the range, inclusive
  * @param formatter      given date format
  */
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
      val to: LocalDate = LocalDate.parse(toDateString, formatter)
      if (from.isAfter(to)) {
        errors += ValidationError(message = s"Invalid date range: from [$fromDateString], to [$toDateString]")
      }
    }
    errors.toSet
  }

}
