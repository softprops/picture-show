package pictureshow

trait Config { self: Resolver =>
  import java.io.{File => JFile}
  
  /** name of config file */
  def configName = "conf.js"
  /** title of show */
  def showTitle = provided.title.getOrElse("picture show")
  /** list of sections to render */
  def sections = provided.sections.getOrElse("test" :: Nil)
  /** ??? */
  def resourceBase = ""
  
  class SlurpableFile(f: JFile) {
    lazy val slurp = scala.io.Source.fromFile(f).getLines.mkString("")
  }
  implicit def f2sf(f: JFile) = new SlurpableFile(f)
  case class Config(title: Option[String], sections: Option[List[String]])
  private val provided = Files(loadPath :: configName :: Nil) match {
    case Some(f) =>
      val json = scala.util.parsing.json.JSON.parse(f.slurp)
      def extract[T](name: String)(f: Any => T) = json flatMap {
        case head :: (k, v) :: Nil if(k == name) => Some(f(v))
        case (k, v) :: tail if(k == name) => Some(f(v))
        case _ => None
      }
      Config(extract("title") { _.toString }, extract("sections") { v => v.asInstanceOf[List[String]] })
    case _ => Config(None, None)
  }
}