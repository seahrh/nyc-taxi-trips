package v1

import org.scalatestplus.play.PlaySpec

class v1Spec extends PlaySpec {

  "v1#roundUp" should {

    "have the correct number of decimal places" in {
      roundUp(1.23456789, decimalPlaces = 0) mustEqual BigDecimal(1.0)
      roundUp(1.23456789, decimalPlaces = 2) mustEqual BigDecimal(1.23)
      roundUp(1.23456744, decimalPlaces = 6) mustEqual BigDecimal(1.234567)
    }

    "round half up" in {
      roundUp(1.23499999, decimalPlaces = 2) mustEqual BigDecimal(1.23)
      roundUp(1.235, decimalPlaces = 2) mustEqual BigDecimal(1.24)
    }
  }
}
