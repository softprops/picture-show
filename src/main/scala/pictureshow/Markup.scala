package pictureshow

trait Markup { self: IO with Resolver with Config =>
  import com.tristanhunt.knockoff.DefaultDiscounter._
  def asset(path: String) =
    "assets/" + (
      if (loadPath.contains("/")) path.substring(loadPath.lastIndexOf("/")) 
      else path
    )
  /** combine all js assets */
  def combineJs =  loadJs(resourceBase) map asset
  /** combine all css assets */
  def combineCss = loadCss(resourceBase) map asset
  /** combine both js and css header assets */
  def combineHeads = combineJs ::: combineCss
  /** loads and processes all markdown from configured sections */
  def mkSlides = {
    ((new xml.NodeBuffer, 0) /: sections) ((a, s) => {
      val files = loadContent(s)
      val fileRes = (((new scala.xml.NodeBuffer, a._2)  /: files)((m, f) => {
        val mdRes = md(f, m._2)
        (m._1 &+ mdRes._1, mdRes._2)
      }))
      (a._1 &+ fileRes._1, fileRes._2)
    })._1
  }
  private def md(fname: String, index: Int) = {
    /** s/fromFile/fromPath in scala 2.8 */
    val content = scala.io.Source.fromFile(fname, "utf8").getLines.mkString("\n")
    val slides = content.split("!SLIDE")
    ((new xml.NodeBuffer, index) /: slides)( (a, s) => {
      val lines = s.split("\n")
      (a._1 &+ (<div class="content" id={"slide-%s" format a._2}>
       { parse(s) }
      </div>), a._2 + 1)
    })
  }
  private def parse(content: String) = toXHTML(knockoff(content))
}