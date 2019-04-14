package util

import play.api.Logger

trait Logging {
  protected val log: Logger = Logger(this.getClass)
}
