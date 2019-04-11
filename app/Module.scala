import javax.inject._
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import v1.post._
import v1.trip.averagespeed.{AverageSpeedRepository, AverageSpeedRepositoryImpl}

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  override def configure(): Unit = {
    bind[PostRepository].to[PostRepositoryImpl].in[Singleton]
    bind[AverageSpeedRepository].to[AverageSpeedRepositoryImpl].in[Singleton]
  }
}
