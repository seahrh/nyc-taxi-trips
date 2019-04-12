package v1.trip

import akka.stream.Materializer
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers._
import play.api.test._

class TripApplicationSpec extends PlaySpec with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer

  "Trip Application" should {

    "return 404 Not Found when path does not exist" in {
      val request = FakeRequest(GET, "/bad_path")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual NOT_FOUND
      }
    }

  }

}
