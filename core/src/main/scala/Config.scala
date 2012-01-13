package pictureshow

trait Config { self: IO with Logging =>
  import scala.util.parsing.json.JSON

  val configName = "conf.js"
  /** title of show */
  def showTitle = provided.title.getOrElse("picture show")
  /** list of sections to render */
  def sections = provided.sections.getOrElse(Nil)

  case class Show(title: Option[String], sections: Option[List[String]])
  private val provided = {
    slurp(configName) map { contents =>
      lazy val json = JSON.parseFull(contents)

      def extract[T](name: String)(f: Any => T) = json flatMap {
        case m: Map[String, Any] if(m isDefinedAt name) => Some(f(m(name)))
        case e => println(e); None
      }

      json match {
        case None => sys.error("Invalid json config %s: %s" format(configName, contents))
        case _ => Show(extract("title") { _.toString }, extract("sections") { v => v.asInstanceOf[List[String]] })
      }
    } getOrElse {
      sys.error("Invalid json in file %s" format configName)
    }
  }
}
