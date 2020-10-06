package cup.services

import cup.models.UsZipCode
import org.slf4j.MDC
import play.api.Logger

case class GeoZipCodeService(url: String = "") {

  private val logger = Logger(this.getClass)

  def verifyZipCode(rid: String, zipCode: String): Either[String, UsZipCode] = {
    MDC.put("rid", rid)
    logger.info(s"verifyZipCode: $zipCode")
    // https://m.usps.com/m/QuickZipAction?mode=2&tZip=01085&jsonInd=Y
    val baseUrl = url + "/m/QuickZipAction?mode=2&tZip=" + zipCode + "&jsonInd=Y"

    logger.info(s"verifyZipCode completed")
    Left("Not supported")

  }
}
