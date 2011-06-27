package pictureshow

object LoggingPackage {
  type Logger = { def println(msg: String): Unit }
}
import LoggingPackage._

trait Logging {
  lazy val logger: Logger = System.out
  def log(msg: String) = if(!msg.isEmpty) logger.println(formattedLogEntry(msg))
  def formattedLogEntry(msg: String)  = "picture show [%s] %s" format(
    new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new java.util.Date), msg
  )
}