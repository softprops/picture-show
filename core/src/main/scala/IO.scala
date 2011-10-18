package pictureshow

trait IO { self: Resolver =>
  import java.net.URL
  def exists(path: String) = slurp(path).isDefined
  def slurp(path: String) = try {
    Some(scala.io.Source.fromURL(new URL(loadPath, path), "utf-8").mkString(""))
  } catch {
    case e: java.io.FileNotFoundException => None
  }
}
