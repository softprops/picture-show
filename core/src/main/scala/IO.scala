package pictureshow

object IO {
  import java.net.URL
  def slurp(url: URL) = try {
    Some(scala.io.Source.fromURL(url, "utf-8").mkString(""))
  } catch {
    case e: java.io.FileNotFoundException => None
  }
}
