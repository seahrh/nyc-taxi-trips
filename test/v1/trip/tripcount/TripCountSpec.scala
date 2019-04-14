package v1.trip.tripcount

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import dal.{BigQueryRepository, BigQueryRepositoryImpl, TripCount}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.MarkerContext
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import v1._

import scala.concurrent.Future

class TripCountSpec extends PlaySpec with MockitoSugar {

  private val baseUrl: String = "/total_trips"

  private val result: Seq[TripCount] = Seq(
    TripCount("2019-04-01", 111), //scalastyle:ignore
    TripCount("2019-04-02", 222), //scalastyle:ignore
    TripCount("2019-04-03", 333), //scalastyle:ignore
    TripCount("2019-04-04", 444), //scalastyle:ignore
    TripCount("2019-04-05", 555) //scalastyle:ignore
  )

  private val repo = mock[BigQueryRepositoryImpl]
  when(repo.dateFormat) thenReturn  DateTimeFormatter.ISO_LOCAL_DATE
  when(repo.tripCount(any[LocalDate], any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(result)

  private val app = new GuiceApplicationBuilder()
    .overrides(bind[BigQueryRepository].toInstance(repo))
    .build

  private val repoNoData = mock[BigQueryRepositoryImpl]
  when(repoNoData.dateFormat) thenReturn  DateTimeFormatter.ISO_LOCAL_DATE
  when(repoNoData.tripCount(any[LocalDate], any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(
    Seq.empty[TripCount]
  )

  private val appNoData = new GuiceApplicationBuilder()
    .overrides(bind[BigQueryRepository].toInstance(repoNoData))
    .build

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
      for (res <- route(appNoData, request)) {
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
