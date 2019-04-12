package v1.trip

import akka.stream.Materializer
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers._
import play.api.test._
import v1._
import v1.trip.averagespeed.AverageSpeedResource

class TripAverageSpeedSpec extends PlaySpec with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer

  "Trip Average Speed" should {
    "return 200 OK and resource when resource exists" in {
      val request = FakeRequest(GET, "/average_speed_24hrs?date=2019-04-01")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual OK
        val payload: SuccessPayload = contentAsJson(res).as[SuccessPayload]
        val data: Seq[AverageSpeedResource] = payload.data.as[Seq[AverageSpeedResource]]
        data.isEmpty mustEqual false
      }
    }

    "return 404 Not Found and details when resource does not exist" in {
      val request = FakeRequest(GET, "/average_speed_24hrs?date=2019-04-06")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual NOT_FOUND
        val payload: FailurePayload = contentAsJson(res).as[FailurePayload]
        payload.error.message must startWith("Average speed not found")
      }
    }

    "return 404 Not Found when path does not exist" in {
      val request = FakeRequest(GET, "/bad_path")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual NOT_FOUND
      }
    }

    "return 400 Bad Request and details when input value is invalid" in {
      val request = FakeRequest(GET, "/average_speed_24hrs?date=2019-02-31")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        val payload: FailurePayload = contentAsJson(res).as[FailurePayload]
        payload.error.message must startWith("Invalid date")
      }
    }

    "return 400 Bad Request when input key is invalid" in {
      val request = FakeRequest(GET, "/average_speed_24hrs?name=2019-02-01")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        contentAsString(res) mustEqual "Missing parameter: date"
      }
    }


  }

}
