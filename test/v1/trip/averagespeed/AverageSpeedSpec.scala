package v1.trip.averagespeed

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import dal.{AverageSpeed, BigQueryRepository, BigQueryRepositoryImpl}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.MarkerContext
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import v1._

import scala.concurrent.Future

class AverageSpeedSpec extends PlaySpec with MockitoSugar {
  private val baseUrl: String = "/average_speed_24hrs"

  private val repo = mock[BigQueryRepositoryImpl]
  when(repo.dateFormat) thenReturn  DateTimeFormatter.ISO_LOCAL_DATE
  when(repo.avgSpeed(any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(
    Option(AverageSpeed("2019-04-01", 1.1F)))

  private val app = new GuiceApplicationBuilder()
    .overrides(bind[BigQueryRepository].toInstance(repo))
    .build

  private val repoNoData = mock[BigQueryRepositoryImpl]
  when(repoNoData.dateFormat) thenReturn  DateTimeFormatter.ISO_LOCAL_DATE
  when(repoNoData.avgSpeed(any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(None)

  private val appNoData = new GuiceApplicationBuilder()
    .overrides(bind[BigQueryRepository].toInstance(repoNoData))
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
