//package sbtbuildinfo

import java.awt.Desktop
import java.io.IOException
import java.net.{URI, URISyntaxException}

import scala.sys.process.Process

object BuildUtil {

  def openBrowser(url:String) {
    val os:String = System.getProperty("os.name").toLowerCase().substring(0, 3)
    System.out.println(s"OS: $os")
    os match {
      case ("win") => openWin(url)
      case "mac" => openMac(url)
      case "nix" => openLinx(url)
      case "nux" => openLinx(url)
      case _ => openDefault(url)
    }
  }

  private def openLinx(url:String): Unit = {

  }

  private def openMac(url:String): Unit = {
    System.out.println(s"openMac: $url")
    try {
      val rt = Runtime.getRuntime;
      rt.exec("open " + url);
    } catch {
      case e:Throwable =>
        e.printStackTrace()
        Process(s"open -a /Application/Google Chrome.app $url")
    }
  }

  private def openWin(url:String): Unit = {
    System.out.println(s"openWin: $url")
    val rt = Runtime.getRuntime;
    rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
  }

  private def openDefault(url:String): Unit = {
    System.out.println(s"openDefault: $url")
    if (Desktop.isDesktopSupported) {
      val desktop = Desktop.getDesktop
      try {
        desktop.browse(new URI(url))
      } catch {
        case e@(_: IOException | _: URISyntaxException) =>
          // TODO Auto-generated catch block
          e.printStackTrace
      }
    }
    else {
      val runtime = Runtime.getRuntime
      try runtime.exec("xdg-open " + url)
      catch {
        case e: IOException =>
          e.printStackTrace
      }
    }


  }

  private def open(url:String) {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      //Desktop.getDesktop().browse(new URI("http://www.example.com"));
      Desktop.getDesktop().browse(new URI(url))
    }
  }

}
