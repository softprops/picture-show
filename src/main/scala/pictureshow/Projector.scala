package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._

class Projector extends Config with Resolver with IO with Markup with Templates with unfiltered.Plan {
  def filter = {
    case GET(Path("/", _)) => ResponseString(render(css(combineCss), mkSlides).toString)
  }
}

object Server {
  import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler}
  def main(args: Array[String]) {
    
    val server = unfiltered.server.Http(3000)  
    
    val files = new ResourceHandler
    files.setResourceBase(".")
    val context = new ContextHandler
    context.setContextPath("/assets")
    context.setAliases(true)
    context.setHandler(files)
    
    server.handler(c => context)
    server.filter(new Projector)
    
    server.start
  }
}

/** todo read from config file */
trait Config { self: Resolver =>
  def sections = "test" :: Nil
  def resourceBase = "."
  def showTitle = "picture show"
}

trait Templates { self: Config =>
  /** render stylesheet links */
  def css(sheets: Seq[String]) = (new xml.NodeBuffer  /: sheets) ((m, s) => {
    m &+ <link rel="stylesheet" type="text/css" href={s} />
  })
  /** render script tags */
  def js(scripts: Seq[String]) = (new xml.NodeBuffer  /: scripts) ((m, s) => {
    m &+ <script type="text/javascript" src={s} ></script>
  })
  /** render the show */
  def render(slides : xml.NodeBuffer) = default(new xml.NodeBuffer, slides)
  /** render the show with custom header assets */
  def render(heads : xml.NodeBuffer, slides: xml.NodeBuffer) = default(heads, slides)
  /** default template */
  private def default(heads: xml.NodeBuffer, slides: xml.NodeBuffer) = 
    <html>
      <head>
        <title>{ showTitle }</title>
        <link rel="stylesheet" type="text/css" href="assets/show/css/show.css" />
        <script type="text/javascript" src="assets/show/js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="assets/show/js/show.js"></script>
        { heads }
      </head>
      <body>
        <div id="slides">
        <div id="track">
          { slides }
        </div> 
       </div>
      </body>
    </html>
}

trait Resolver {
  def loadPath = "show"
}

trait Markup { self: IO with Config =>
  import com.tristanhunt.knockoff.DefaultDiscounter._
  
  def combineJs =  loadJs(resourceBase) map { "assets/" + _ }
  def combineCss = loadCss(resourceBase) map { "assets/" + _ }
  def combineHeads = combineJs ::: combineCss
  
  def mkSlides = {
    (new xml.NodeBuffer /: sections)((l, s) => {
      val files = loadContent(s)
      l &+ ((new scala.xml.NodeBuffer  /: files)((m, f) => {
        m &+ md(f)
      }))
    })
  }
  
  def md(fname: String) = {
    /** s/fromFile/fromPath in scala 2.8 */
    val content = scala.io.Source.fromFile(fname, "utf8").getLines.mkString("\n")
    val slides = content.split("!SLIDE")
    (new xml.NodeBuffer /: slides)((nodes, s) => {
      val lines = s.split("\n")
      val meta = List("")
      val transition = "none"
      nodes &+ (<div class="content" id={"slide-%s" format nodes.size} rel={"transition-%s" format transition}>
       { parse(s) }
      </div>)
    })
  }
  
  def parse(content: String) = toXHTML(knockoff(content))
}

trait IO { self: Resolver =>
  /** s/sort/sortWith in scala 2.8 */
  def loadContent(section: String) =
    Files.ls(sectionPath(section)) { _.endsWith(".md") } sort(_<_)
  def loadJs(section: String) =
    Files.ls(sectionPath(section)) { _.endsWith(".js") }
  def loadCss(section: String) =
    Files.ls(sectionPath(section)) { _.endsWith(".css") }
  private def sectionPath(sec: String) = (loadPath :: sec :: Nil) mkString("/")
}

object Files { //self: Resolver =>
 /** creator */
 def apply(path: String) = new java.io.File(path)
 /** recursivly lists file paths */ 
 def ls(path: String)(f: String => Boolean): List[String] = {
   val root = new java.io.File(path)
   (root.isDirectory match {
      case true => (List[String]() /: (root.listFiles.toList map { _.getPath })) ((s, p) => ls(p)(f) ::: s)
      case _ => root.getPath :: Nil
    }).filter(f)
 }
}