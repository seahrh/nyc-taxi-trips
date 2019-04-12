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
    "return 200 OK and data when data exists" in {
      val request = FakeRequest(GET, "/average_speed_24hrs?date=2019-04-01")
        .withHeaders(HOST -> "localhost:8080")
      route(app, request) match {
        case Some(res) =>
          status(res) mustEqual OK
          //contentAsString(res) mustEqual ""
          //contentAsJson(res) mustEqual Json.obj()
          val payload: SuccessPayload = contentAsJson(res).as[SuccessPayload]
          val data: Seq[AverageSpeedResource] = payload.data.as[Seq[AverageSpeedResource]]
          data.isEmpty mustEqual false
        case _ => throw new IllegalStateException("invalid route")
      }
    }
  }

}
