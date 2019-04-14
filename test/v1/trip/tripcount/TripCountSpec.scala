package v1.trip.tripcount

import akka.stream.Materializer
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers._
import play.api.test._
import v1._

class TripCountSpec extends PlaySpec with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer

  private val baseUrl: String = "/total_trips"

  "Total Trips" should {

    "return 200 OK and resource when resource exists" in {
      val request = FakeRequest(GET, s"$baseUrl?start=2019-04-01&end=2019-04-03")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual OK
        val payload: SuccessPayload = contentAsJson(res).as[SuccessPayload]
        val data: Seq[Resource] = payload.data.as[Seq[Resource]]
        data.isEmpty mustEqual false
      }
    }

    "return 404 Not Found and details when resource does not exist" in {
      val request = FakeRequest(GET, s"$baseUrl?start=2029-04-06&end=2029-04-07")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual NOT_FOUND
        val payload: FailurePayload = contentAsJson(res).as[FailurePayload]
        payload.error.message must startWith("Total trips not found")
      }
    }

    "return 400 Bad Request and details when input date is invalid" in {
      val request = FakeRequest(GET, s"$baseUrl?start=2019-02-28&end=2019-02-31")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        val payload: FailurePayload = contentAsJson(res).as[FailurePayload]
        payload.error.message must startWith("Invalid date")
      }
    }

    "return 400 Bad Request and details when input date range is invalid" in {
      val request = FakeRequest(GET, s"$baseUrl?start=2019-04-03&end=2019-04-01")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        val payload: FailurePayload = contentAsJson(res).as[FailurePayload]
        payload.error.message must startWith("Invalid date range")
      }
    }

    "return 400 Bad Request and details when input key is missing" in {
      var request = FakeRequest(GET, s"$baseUrl?foo=2019-04-01&end=2019-04-03")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        contentAsString(res) mustEqual "Missing parameter: start"
      }
      request = FakeRequest(GET, s"$baseUrl?start=2019-04-01&foo=2019-04-03")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        contentAsString(res) mustEqual "Missing parameter: end"
      }
    }
  }

}
