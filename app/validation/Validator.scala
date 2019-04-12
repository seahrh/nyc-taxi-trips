package validation

trait Validator {

  def validate: Set[ValidationError]

}
