package cup.models

import cup.utils.JsonHelper
import play.api.libs.json.JsObject

case class Address(name: String,
                   address1: String,
                   address2: Option[String],
                   city: String,
                   cityStatus: String,
                   state: String,
                   zipStatus: String,
                   zip: String) {

  override def toString: String = {
    s"name:$name address1:$address1 city: $city state: $state zip: $zip"
  }
}

object Address {

  val NAME_FIELD: String = "co"
  val ADDRESS_1_FIELD: String = "address1"
  val ADDRESS_2_FIELD: String = "address2"
  val CITY_FIELD: String = "city"
  val CITY_STATUS_FIELD: String = "cityStatue"
  val STATE_FIELD: String = "state"
  val ZIP_STATUS_FIELD: String = "zipStatus"
  val ZIP_CODE_FIELD: String = "zip"

  def getAddress(json: JsObject): Address = {
    val jsonHelper = JsonHelper(json)
    val name = jsonHelper.getDefaultValue(NAME_FIELD).getOrElse("")
    val address1 = jsonHelper.getDefaultValue(ADDRESS_1_FIELD).getOrElse("")
    val address2 = jsonHelper.getDefaultValue(ADDRESS_2_FIELD)
    val city = jsonHelper.getDefaultValue(CITY_FIELD).getOrElse("")
    val cityStatus = jsonHelper.getDefaultValue(CITY_STATUS_FIELD).getOrElse("")
    val state = jsonHelper.getDefaultValue(STATE_FIELD).getOrElse("")
    val zipState = jsonHelper.getDefaultValue(ZIP_STATUS_FIELD).getOrElse("")
    val zip = jsonHelper.getDefaultValue(ZIP_CODE_FIELD).getOrElse("")

    Address(name, address1, address2, city, cityStatus, state, zipState, zip)
  }

}
