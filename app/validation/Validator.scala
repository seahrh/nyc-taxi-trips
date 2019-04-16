package validation

/**
  * Interface for classes performing validation
  */
trait Validator {

  /**
    * Validates some given data, and returns a set of validation errors, if any.
    *
    * @return Set of validation errors, if any
    */
  def validate: Set[ValidationError]

}
