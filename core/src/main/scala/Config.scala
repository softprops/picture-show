package pictureshow

trait Config { self: Resolver /*IO*/ =>
  import scala.util.parsing.json.JSON

  /** title of show */
  def showTitle = provided.title.getOrElse("picture show")
  /** list of sections to render */
  def sections = provided.sections.getOrElse(Nil)

  case class Show(title: Option[String], sections: Option[List[String]])

  private val provided = {
    lazy val json = JSON.parseFull(configuration)
    def extract[T](name: String)(f: Any => T) = json flatMap {
      case m: Map[String, Any] if(m isDefinedAt name) => Some(f(m(name)))
      case e =>  None
    }
    json match {
      case None => sys.error("malformed configuration '%s'" format configuration)
      case _ => Show(
        extract("title") { _.toString },
        extract("sections") { v => v.asInstanceOf[List[String]] })
    }
  }
}
