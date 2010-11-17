package pictureshow

trait Config { self: IO with Logging =>
  /** name of config file */
  def configName = "conf.js"
  /** title of show */
  def showTitle = provided.title.getOrElse("picture show")
  /** list of sections to render */
  def sections = provided.sections.getOrElse(Nil)
  /** ??? */
  def resourceBase = ""
  
  case class Config(title: Option[String], sections: Option[List[String]])
  private val provided = {
    slurp(configName) map { contents =>
      val json = scala.util.parsing.json.JSON.parse(contents)
      def extract[T](name: String)(f: Any => T) = json flatMap { _.flatMap {
        case (k, v) if(k == name) => Some(f(v))
        case _ => None
      }.firstOption }
      
      json match {
        case None => error("invalid json config %s" format(contents))
        case _ => Config(extract("title") { _.toString }, extract("sections") { v => v.asInstanceOf[List[String]] })
      }
    } getOrElse {
      error("invalid json in file %s" format configName)
    }
  }
}