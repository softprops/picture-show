package pictureshow

import org.json4s._
import org.json4s.native.JsonMethods._

trait Config { self: Resolver /*IO*/ =>
  /** title of show */
  def showTitle = provided.map(_.title).getOrElse("picture show")
  /** list of sections to render */
  def sections = provided.map(_.sections).getOrElse(Nil)

  case class Show(title: String, sections: List[String])

  private val provided: Option[Show] = {
    val js = parse(configuration)
    (for {
      JObject(fs) <- parse(configuration)
      ("title", JString(title)) <- fs
      ("sections", JArray(sections)) <- fs
      JString(section) <- sections
    } yield (title, for { JString(section) <- sections } yield section))
      .headOption
      .map {
        case (title, sections) => Show(title, sections)
      }.orElse(sys.error("malformed configuration %s" format(configuration)))
  }
}
