package cup.models

import cup.utils.JsonHelper
import play.api.libs.json.JsObject

import scala.collection.mutable.ListBuffer

case class UsZipCode(deliverable:Boolean, exactCompanyMatch:Boolean, formExcepts:String, address: List[Address]) {

}

object UsZipCode {
  val DELIVERABLE_FIELD:String = "deliverable"
  val EXACT_COMPANY_MATCH_FIELD:String = "exactCompanyMatch"
  val FORM_EXCEPTS_FIELD:String = "formExcepts"
  val ADDRESS_OBJECT_FIELD:String = "addresses"

  def getUsZipCode(json:JsObject):UsZipCode = {
    val jsonHelper = JsonHelper(json)
    val deliverable = jsonHelper.getBooleanValue(DELIVERABLE_FIELD).getOrElse(false)
    val exactMatch = jsonHelper.getBooleanValue(EXACT_COMPANY_MATCH_FIELD).getOrElse(false)
    val formExcepts = jsonHelper.getDefaultValue(FORM_EXCEPTS_FIELD).getOrElse("")

    val address = ListBuffer.empty[Address]

    for ( addy <- (json \\ ADDRESS_OBJECT_FIELD)) {
      address += Address.getAddress(addy.as[JsObject])
    }

    UsZipCode(deliverable, exactMatch, formExcepts, address.toList)

  }
}
