import dal.{TripRepository, BigQueryTripRepository}
import com.google.inject.AbstractModule
import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  override def configure(): Unit = {
    bind[TripRepository].to[BigQueryTripRepository].in[Singleton]
  }
}
