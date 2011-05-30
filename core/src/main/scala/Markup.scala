package pictureshow

trait Markup { self: IO with Resolver with Config with Logging =>
  import com.tristanhunt.knockoff.DefaultDiscounter._
  import java.net.URL
  def asset(path: String) = path
  /** combine all js assets */
  def combineJs = ("js/custom.js" :: Nil) filter exists map asset
  /** combine all css assets */
  def combineCss = ("css/custom.css" :: Nil) filter exists map asset
  /** loads and processes all markdown from configured sections */
  def mkSlides = {
    ((new xml.NodeBuffer, 0) /: sections) ((a, s) => {
      val files = slurp("%s/%s.md" format (s, s))
      if(files.isEmpty) log("no file(s) at path %s/%s.md" format(s, s))
      val fileRes = (((new scala.xml.NodeBuffer, a._2)  /: files)((m, f) => {
        val mdRes = parse(f, m._2)
        (m._1 &+ mdRes._1, mdRes._2)
      }))
      (a._1 &+ fileRes._1, fileRes._2)
    })._1
  }
  private def parse(content: String, index: Int) = {
    var Meta = """(?i)(HTML)""".r
    // (?m) enables multiline matching, so ^ anchors to start of line,
    // not start of input
    val slides = content.split("(?m)^!SLIDE")
    if(slides.isEmpty) log("no slides within file %s at index %s" format(content, index))
    ((new xml.NodeBuffer, index) /: slides.drop(1))( (a, s) => {
      (a._1 &+ (<div class="content" id={"slide-%s" format a._2}>
       <div class="container">{
         s.split("\n")(0).trim match {
            case Meta(meta) => {
              <div>{ xml.PCData( s.split("\n").toList.drop(1).mkString("") ) }</div>
            }
            case _ => parseMarkdown(s)
          }
        }</div>
      </div>), a._2 + 1)
    })
  }

  private def parseMarkdown(content: String) = toXHTML(knockoff(content))
}
