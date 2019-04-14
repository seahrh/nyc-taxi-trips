package geom

import com.google.common.geometry.S2CellId
import org.scalatestplus.play.PlaySpec

class geomSpec extends PlaySpec {

  "geom#s2id" should {

    "return the same value in repeated runs" in {
      val lat: Float = 1.276162F
      val lng: Float = 103.847333F
      val level: Int = 16
      val e = s2id(lat, lng, level)
      for (_ <- 0 until 2) {
        s2id(lat, lng, level) mustEqual e
      }
    }

    "contain the given latitude-longitude when the returned value is a parent cell" in {
      val lat: Float = 1.276162F
      val lng: Float = 103.847333F
      val level: Int = 16
      val parentToken: String = s2id(lat, lng, level)
      val parent: S2CellId = S2CellId.fromToken(parentToken)
      val childToken: String = s2id(lat, lng) // leaf cell
      val child: S2CellId = S2CellId.fromToken(childToken)
      parent.contains(child) mustEqual true
    }
  }
}
