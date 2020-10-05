package cup.utils

import play.api.Logger
import play.api.libs.json.JsObject

case class JsonHelper(json:JsObject) {

  private val logger = Logger(this.getClass)

  def searchValue(key: String): Option[String] = {
    val value = (json \\ key).mkString(",")
    val result = if (value.length > 0) {
      Some(value.replace("\"", "").trim())
    } else {
      None
    }
    result
  }

  def getDefaultValue(key: String): Option[String] = {
    val text = (json \ "content" \ key).asOpt[String]
    if (text.isDefined) {
      logger.debug(s"$key found: ${text.get}")
    } else {
      logger.debug(s"$key NOT found")
    }
    text
  }

  def getIntValue(key: String): Option[Int] = {
    val tempValue = searchValue(key)
    if (tempValue.isDefined) {
      val value = tempValue.get.toInt
      logger.info(s"$key found: $value")
      Some(value)
    } else {
      logger.info(s"$key INT not found") // , in ${json.toString}")
      None
    }
  }

  def getBooleanValue(key: String): Option[Boolean] = {
    val tempValue = searchValue(key)
    if (tempValue.isDefined) {
      val value = tempValue.get.toBoolean
      logger.info(s"$key found: $value")
      Some(value)
    } else {
      logger.warn(s"$key Boolean not found") // , in ${json.toString}")
      None
    }
  }

}
