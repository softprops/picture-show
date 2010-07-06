package pictureshow

trait Markup { self: IO with Resolver with Config =>
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
      val fileRes = (((new scala.xml.NodeBuffer, a._2)  /: files)((m, f) => {
        val mdRes = md(f, m._2)
        (m._1 &+ mdRes._1, mdRes._2)
      }))
      (a._1 &+ fileRes._1, fileRes._2)
    })._1
  }
  private def md(content: String, index: Int) = {
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