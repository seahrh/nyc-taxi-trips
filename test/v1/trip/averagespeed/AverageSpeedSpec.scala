package v1.trip.averagespeed

import java.io.FileInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import dal.{AverageSpeed, BigQueryTripRepository, TripRepository}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.MarkerContext
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import v1._

import scala.concurrent.Future

class AverageSpeedSpec extends PlaySpec with MockitoSugar {
  private val baseUrl: String = "/average_speed_24hrs"

  private val data: Option[AverageSpeed] = {
    val stream = new FileInputStream("test/resources/average_speed.json")
    val json: JsValue = try {
      Json.parse(stream)
    } finally {
      stream.close()
    }
    json.as[Seq[AverageSpeed]].headOption
  }

  private val repo = mock[BigQueryTripRepository]
  when(repo.dateFormat) thenReturn DateTimeFormatter.ISO_LOCAL_DATE
  when(repo.avgSpeed(any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(data)

  private val app = new GuiceApplicationBuilder()
    .overrides(bind[TripRepository].toInstance(repo))
    .build

  private val repoNoData = mock[BigQueryTripRepository]
  when(repoNoData.dateFormat) thenReturn DateTimeFormatter.ISO_LOCAL_DATE
  when(repoNoData.avgSpeed(any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(None)

  private val appNoData = new GuiceApplicationBuilder()
    .overrides(bind[TripRepository].toInstance(repoNoData))
    .build

  "Average Speed" should {

    "return 200 OK and resource when resource exists" in {
      val request = FakeRequest(GET, s"$baseUrl?date=2019-04-01")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual OK
        val payload: SuccessPayload = contentAsJson(res).as[SuccessPayload]
        val data: Seq[Resource] = payload.data.as[Seq[Resource]]
        data.isEmpty mustEqual false
      }
    }

    "return 404 Not Found and details when resource does not exist" in {
      val request = FakeRequest(GET, s"$baseUrl?date=2019-04-06")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(appNoData, request)) {
        status(res) mustEqual NOT_FOUND
        val payload: FailurePayload = contentAsJson(res).as[FailurePayload]
        payload.error.message must startWith("Average speed not found")
      }
    }

    "return 400 Bad Request and details when input value is invalid" in {
      val request = FakeRequest(GET, s"$baseUrl?date=2019-02-31")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        val payload: FailurePayload = contentAsJson(res).as[FailurePayload]
        payload.error.message must startWith("Invalid date")
      }
    }

    "return 400 Bad Request and details when input key is invalid" in {
      val request = FakeRequest(GET, s"$baseUrl?name=2019-02-01")
        .withHeaders(HOST -> "localhost:8080")
      for (res <- route(app, request)) {
        status(res) mustEqual BAD_REQUEST
        contentAsString(res) mustEqual "Missing parameter: date"
      }
    }


  }

}
