import com.google.common.geometry.{S2CellId, S2LatLng}

package object geom {

  private val S2_CELL_LEVEL_MAX: Int = 30

  /**
    * Converts latitude-longitude degree coordinates to a S2 cell ID at the required level.
    *
    * @param latitude latitude in degrees
    * @param longitude longitude in degrees
    * @param level S2 cell level
    * @return S2 cell ID
    */
  def s2id(latitude: Float, longitude: Float, level: Int = S2_CELL_LEVEL_MAX): String = {
    val ll = S2LatLng.fromDegrees(latitude.toDouble, longitude.toDouble)
    S2CellId.fromLatLng(ll).parent(level).toToken
  }

}
