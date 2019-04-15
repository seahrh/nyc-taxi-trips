package v1.trip.averagefareheatmap

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import dal.{AverageFareByPickupLocation, TripRepository, BigQueryTripRepository}
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

class AverageFareHeatmapSpec extends PlaySpec with MockitoSugar {

  private val baseUrl: String = "/average_fare_heatmap"

  private val result: Seq[AverageFareByPickupLocation] = Seq(
    AverageFareByPickupLocation(1.276162F, 103.847333F, 1.11F),
    AverageFareByPickupLocation(1.276162F, 103.847333F, 2.22F),
    AverageFareByPickupLocation(1.276162F, 103.847333F, 3.33F),
    AverageFareByPickupLocation(1.276162F, 103.847333F, 4.44F),
    AverageFareByPickupLocation(1.276162F, 103.847333F, 5.55F)
  )

  private val repo = mock[BigQueryTripRepository]
  when(repo.dateFormat) thenReturn  DateTimeFormatter.ISO_LOCAL_DATE
  when(repo.avgFareByPickupLocation(any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(result)

  private val app = new GuiceApplicationBuilder()
    .overrides(bind[TripRepository].toInstance(repo))
    .build

  private val repoNoData = mock[BigQueryTripRepository]
  when(repoNoData.dateFormat) thenReturn  DateTimeFormatter.ISO_LOCAL_DATE
  when(repoNoData.avgFareByPickupLocation(any[LocalDate])(any[MarkerContext])) thenReturn Future.successful(
    Seq.empty[AverageFareByPickupLocation]
  )

  private val appNoData = new GuiceApplicationBuilder()
    .overrides(bind[TripRepository].toInstance(repoNoData))
    .build

  "Average Fare Heatmap" should {

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
        payload.error.message must startWith("Average fare not found")
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
