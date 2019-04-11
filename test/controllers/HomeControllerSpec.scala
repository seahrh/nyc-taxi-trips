import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "HomeController" should {

    "render the index page" in {
      val request = FakeRequest(GET, "/index").withHeaders(HOST -> "localhost:8080").withCSRFToken
      @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
      val home = route(app, request).get

      contentAsString(home) must include ("This is a placeholder page to show you the REST API.")
    }

  }

}