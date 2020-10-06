package cup.utils

/* TODO
 def +=(s: String, x: Any): Unit = {
 x match {
 case String => JsString(attrValue)
 case Long => JsNumber(attrValue)
 case Int => JsNumber(attrValue)
 case Boolean => JsBoolean(attrValue)
 case _ => JsString(attrValue.toString

 seq = seq :+ ((s, JsString(x)))
 }
 */

// java
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone}
import java.util.regex.Pattern

// apache-commons
import org.apache.commons.io.IOUtils

// play
import play.api.libs.json.JsObject
import play.api.Logger
import play.api.libs.json._

trait JsonExporter {

  def toJson: JsObject = {
    val b = new JsonBuilder
    buildJson(b)
    JsObject(b.seq)
  }

  def buildJson(b: JsonBuilder): Unit
}

class JsonBuilder {
  private val logger: Logger = Logger(this.getClass)

  var seq: Seq[(String, JsValue)] =
    scala.collection.immutable.Seq.empty[(String, JsValue)]
  // var seq: Vector[(String, JsValue)] = Vector.empty[(String, JsValue)] - use if appending more than 10 items

  /** remove ALL elements matching s */
  def -=(s: String): Unit = { seq = seq.filterNot(_._1 == s) }

  def keys(): Seq[String] = { seq.map(_._1) }

  def +=(s: String, x: JsValue): Unit = { seq = seq :+ ((s, x)) }
  def +=(s: String, x: Boolean): Unit = { seq = seq :+ ((s, JsBoolean(x))) }
  def +=(s: String, x: Int): Unit = { seq = seq :+ ((s, JsNumber(x))) }
  def +=(s: String, x: Long): Unit = { seq = seq :+ ((s, JsNumber(x))) }
  def +=(s: String, x: Float): Unit = {
    seq = seq :+ ((s, JsNumber(x.toDouble)))
  }
  def +=(s: String, x: Double): Unit = { seq = seq :+ ((s, JsNumber(x))) }
  def +=(s: String, x: String): Unit = { seq = seq :+ ((s, JsString(x))) }

  def +=(s: String, x: Timestamp, iso: Boolean): Unit = {
    val df = if (iso) JsonBuilder.isoDateFormat else JsonBuilder.dateFormat
    seq = seq :+ ((s, JsString(df.format(x))))
  }

  def +=(s: String, x: Option[Timestamp], iso: Boolean): Unit = {
    val df = if (iso) JsonBuilder.isoDateFormat else JsonBuilder.dateFormat
    x match {
      case Some(t) => seq = seq :+ ((s, JsString(df.format(t))))
      case _       => seq = seq :+ ((s, JsNull))
    }
  }

  def +=(s: String, x: JsonExporter): Unit = { seq = seq :+ ((s, x.toJson)) }
  def +=(s: String, x: JsonBuilder): Unit = { seq = seq :+ ((s, x.json)) }
  def +=(s: String, x: JsObject): Unit = { seq = seq :+ ((s, x)) }
  def +=(s: String, x: JsLookupResult): Unit = {
    x match {
      case JsDefined(v) =>
        seq = seq :+ ((s, v))
      case JsUndefined() =>
        logger.warn("Tried to add JsUndefined to JsonBuilder",
                    new Exception(x.toString))
    }
  }
  def +=(s: String, x: Array[Boolean]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(i => JsBoolean(i)))))
  }
  def +=(s: String, x: Array[Int]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(i => JsNumber(i)))))
  }
  def +=(s: String, x: Array[Long]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(i => JsNumber(i)))))
  }
  def +=(s: String, x: Array[Float]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(i => JsNumber(i.toDouble)))))
  }
  def +=(s: String, x: Array[Double]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(i => JsNumber(i)))))
  }
  def +=(s: String, x: Array[String]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(i => JsString(i)))))
  }
  def +=(s: String, x: Array[JsonExporter]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(i => i.toJson))))
  }
  def +=(s: String, x: Array[JsObject]): Unit = {
    seq = seq :+ ((s, JsArray(x)))
  }
  def +=(s: String, x: Seq[Any]): Unit = {
    seq = seq :+ ((s, JsArray(x.map(_ match {
      case s: String       => JsString(s)
      case v: JsValue      => v
      case j: JsonExporter => j.toJson
    }))))
  }

  def json: JsObject = JsObject(seq)
}

object JsonBuilder {
  private val logger: Logger = Logger(this.getClass)

  val dateFormat: SimpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss")

  val dateFormat2: SimpleDateFormat = new SimpleDateFormat("MM-dd-yyyy")
  val dateFormat3: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")

  val isoDateFormat: SimpleDateFormat = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  isoDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))

  val isoDateFormat2: SimpleDateFormat = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ssz")
  isoDateFormat2.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))

  val isoDateFormat3: SimpleDateFormat = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss")
  isoDateFormat3.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))

  def getRequiredValue[T](key: String, json: JsValue)(
      implicit fjs: Reads[T]): T = {
    (json \ key).asOpt[T] match {
      case Some(value) => value
      case _ =>
        throw new Exception("Required " + key + " is missing in " + json)
    }
  }

  def toTimestamp(s: Option[String],
                  defaultValue: Timestamp = new Timestamp(
                    System.currentTimeMillis),
                  fmt: Option[SimpleDateFormat] = None): Timestamp = {
    toTimestampOpt(s, Some(defaultValue), fmt).getOrElse(defaultValue)
  }

  def toTimestampOpt(
      s: Option[String],
      defaultValue: Option[Timestamp] = Some(
        new Timestamp(System.currentTimeMillis)),
      fmt: Option[SimpleDateFormat] = None): Option[Timestamp] = {
    s match {
      case Some(ts) =>
        if (fmt.isDefined) {
          logger.trace(
            s"JsonBuilder.toTimestamp parsing ts: ${ts} using fmt: ${fmt.get.toPattern}")
          Some(new Timestamp(fmt.get.parse(ts).getTime))
        } else if (ts.length >= 20) {
          if (ts.endsWith("Z")) {
            verifyYearFormat(ts, isoDateFormat)
            Some(new Timestamp(isoDateFormat.parse(ts).getTime))
          } else {
            verifyYearFormat(ts, isoDateFormat2)
            Some(new Timestamp(isoDateFormat2.parse(ts).getTime))
          }
        } else if (ts.length == 19) {
          if (ts.indexOf("T") == -1) {
            Some(new Timestamp(dateFormat.parse(ts).getTime))
          } else {
            verifyYearFormat(ts, isoDateFormat3)
            Some(new Timestamp(isoDateFormat3.parse(ts).getTime))
          }
        } else if (ts.length == 10) {
          // To differtiate between YYYY-MM-DD and MM-DD-YYYY formats
          val r = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}")
          val m = r.matcher(ts)
          if (m.find()) {
            Some(new Timestamp(dateFormat3.parse(ts).getTime))
          } else {
            Some(new Timestamp(dateFormat2.parse(ts).getTime))
          }
        } else {
          throw new IllegalArgumentException(s"Unsupported date format: $ts")
        }
      case _ =>
        if (defaultValue.isDefined) {
          defaultValue
        } else {
          throw new IllegalArgumentException("No Date Provided.")
        }
    }
  }

  def verifyYearFormat(date: String, df: SimpleDateFormat): Unit = {
    if (!(Set("19", "20") contains date.substring(0, 2))) {
      throw new IllegalArgumentException(
        "Date %s does not match the Date Format %s".format(date, df.toPattern))
    }
  }

  def convertToUTC(s: String,
                   from_str: String,
                   fmt: SimpleDateFormat): Timestamp = {
    val from_tz: TimeZone = TimeZone.getTimeZone(from_str)
    val to_tz: TimeZone = TimeZone.getTimeZone("UTC")
    val calendar: Calendar = Calendar.getInstance
    val ts_offset = from_tz.getOffset(calendar.getTimeInMillis) - to_tz
      .getOffset(calendar.getTimeInMillis)

    fmt.setTimeZone(from_tz)
    val from_date = fmt.parse(s).getTime

    new Timestamp(from_date - ts_offset)
  }

  def getUTCCSTOffset: Int = {
    val from_tz: TimeZone = TimeZone.getTimeZone("CST")
    val to_tz: TimeZone = TimeZone.getTimeZone("UTC")
    val calendar: Calendar = Calendar.getInstance
    from_tz.getOffset(calendar.getTimeInMillis) - to_tz.getOffset(
      calendar.getTimeInMillis)
  }

  def getUTCESTOffset: Int = {
    val from_tz: TimeZone = TimeZone.getTimeZone("America/New_York")
    val to_tz: TimeZone = TimeZone.getTimeZone("UTC")
    val cal: Calendar = Calendar.getInstance
    from_tz.getOffset(cal.getTimeInMillis) - to_tz.getOffset(
      cal.getTimeInMillis)
  }

  def getUTCPSTOffset: Int = {
    val from_tz: TimeZone = TimeZone.getTimeZone("PST")
    val to_tz: TimeZone = TimeZone.getTimeZone("UTC")
    val cal: Calendar = Calendar.getInstance
    from_tz.getOffset(cal.getTimeInMillis) - to_tz.getOffset(
      cal.getTimeInMillis)
  }

  def getFloat(x: JsValue): Float = {
    if (x.validate[Float].isSuccess) {
      x.asOpt[Float].get
    } else if (x.validate[String].isSuccess) {
      x.asOpt[String].get.toFloat
    } else {
      throw new IllegalArgumentException(x + " does not parse as Float")
    }
  }

  def getJsArray(r: java.io.Reader): JsArray = {
    val b: String = if (r != null) {
      IOUtils.toString(r)
    } else {
      ""
    }

    if (b.length() > 0) {
      var s = b.toString
      if (s.startsWith("CLOB")) {
        s = s.substring(4).trim
      }

      try {
        Json.parse(s).as[JsArray]
      } catch {
        case e: Throwable =>
          logger.error("Cannot parse \"" + s + "\" as Json Array")
          JsArray(Seq.empty[JsValue])
      }
    } else {
      JsArray(Seq.empty[JsValue])
    }
  }

  def getJsObject(r: java.io.Reader): JsObject = {
    val b: String = if (r != null) {
      IOUtils.toString(r)
    } else {
      ""
    }

    if (b.length() > 0) {
      var s = b.toString
      if (s.startsWith("CLOB")) {
        s = s.substring(4).trim
      }

      try {
        Json.parse(s).asInstanceOf[JsObject]
      } catch {
        case e: Throwable =>
          logger.error("Cannot parse \"" + s + "\" as Json Object", e)
          JsObject(Seq.empty[(String, JsValue)])
      }
    } else {
      JsObject(Seq.empty[(String, JsValue)])
    }
  }

  /* is this of any use?
  def getDoubleValue(v: JsValue): Double = {
    if (v.validate[Double].isSuccess)
      v.as[Double]
    else if (v.validate[String].isSuccess)
      v.as[String].toDouble
    else
      throw new Exception("Cannot parse " + v + " as Double")
  } */

  def flatten(key: String,
              value: JsValue,
              map: scala.collection.mutable.Map[String, JsValue])
    : scala.collection.mutable.Map[String, JsValue] = {
    value match {
      case o: JsObject =>
        for ((k, v) <- o.value)
          flatten(key + "." + k, v, map)
      case a: JsArray =>
        val v = a.value
        for (i <- 0 until v.size)
          flatten(key + "(%02d)".format(i), v(i), map)
      case _ => map += key -> value
    }
    map
  }

  def flattenToString(key: String,
                      value: JsValue,
                      map: scala.collection.mutable.Map[String, String])
    : scala.collection.mutable.Map[String, String] = {
    value match {
      case o: JsObject =>
        for ((k, v) <- o.value)
          flattenToString(key + "." + k, v, map)
      case a: JsArray =>
        val v = a.value
        for (i <- 0 until v.size) {
          key match {
            case "lines_of_business" =>
              flattenToString(key + "(%s)".format(
                                (v(i) \ "name").asOpt[String].getOrElse("")),
                              v(i),
                              map)
            case _ => flattenToString(key + "(%02d)".format(i), v(i), map)
          }
        }
      case _ => map += key -> value.toString
    }
    map
  }
}
