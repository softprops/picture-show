package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._

class Projector(path: String) extends Resolver(path) with Config with IO with Markup with Templates with unfiltered.Plan {
  def filter = {
    case GET(Path("/", _)) => ResponseString(render(css(combineCss), mkSlides).toString)
  }
}

object Server {
  import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler}
  def main(args: Array[String]) {
    def assetHandler(contextPath: String, base: String) = {
      val files = new ResourceHandler
      files.setResourceBase(base match {
        case "." => base
        case base => base.toString.substring(0, base.lastIndexOf("/"))
      })
      val context = new ContextHandler
      context.setContextPath(contextPath)
      context.setAliases(true)
      context.setHandler(files)
      context
    }
    
    unfiltered.server.Http(3000)  
      .handler(c => assetHandler("/assets", "."))
      .handler(c => assetHandler("/js", getClass.getResource("js/show.js").toString))
      .handler(c => assetHandler("/css", getClass.getResource("css/show.css").toString))
      .filter(new Projector(args match {
        case Array(p) => p
        case _ => "show"
      }))
      .start
  }
}

/** todo read from config file */
trait Config { self: Resolver =>
  /** name of config file */
  def configName = "conf.js"
  /** list of sections to render */
  def sections = "test" :: Nil
  /** */
  def resourceBase = "."
  /** title of show */
  def showTitle = "picture show"
  private val src = Files(loadPath :: configName :: Nil)
}

trait Templates { self: Config =>
  /** render stylesheet links */
  def css(sheets: Seq[String]) = collectionOf(sheets) { s =>
    <link rel="stylesheet" type="text/css" href={s}/>
  }
  /** render script tags */
  def js(scripts: Seq[String]) = collectionOf(scripts) { s => 
    <script type="text/javascript" src={s}></script>
  }
  /** render the show */
  def render(slides : xml.NodeBuffer) = default(new xml.NodeBuffer, slides)
  /** render the show with custom header assets (css, js, ...) */
  def render(heads : xml.NodeBuffer, slides: xml.NodeBuffer) = default(heads, slides)
  /** builds a collection of nodes */
  private def collectionOf(c: Seq[String])(f: String => xml.Node) =
    (new xml.NodeBuffer  /: c) ((b, e) => { b &+ f(e) })
  /** default template */
  private def default(heads: xml.NodeBuffer, slides: xml.NodeBuffer) = 
    <html>
      <head>
        <title>{ showTitle }</title>
        <link rel="stylesheet" type="text/css" href="css/show.css" />
        <script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="js/show.js"></script>
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

class Resolver(path: String) {
  def loadPath = path
}

trait Markup { self: IO with Resolver with Config =>
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
      nodes &+ (<div class="content" id={"slide-%s" format nodes.size}>
       { parse(s) }
      </div>)
    })
  }
  
  def parse(content: String) = toXHTML(knockoff(content))
}

trait IO { self: Resolver =>
  /** s/sort/sortWith in scala 2.8 */
  def loadContent(section: String) = paths(section, ".md") sort(_<_)
  def loadJs(section: String) = paths(section, ".js")
  def loadCss(section: String) = paths(section, ".css")
  private def paths(section: String, ext: String): List[String] = 
    paths(section) { _.endsWith(ext) }
  private def paths(section: String)(f: String => Boolean) = 
    Files.ls(Files.path(loadPath :: section :: Nil))(f)
}

object Files { //self: Resolver =>
  import java.io.{File => JFile}
  /** build path from parts */
  def path(parts: Seq[String]): String = parts map { _.trim } mkString(System.getProperty("file.separator"))
  /** creator from path */
  def apply(path: String): JFile  = new JFile(path)
  /** creator from parts */
  def apply(parts: Seq[String]): JFile = apply(path(parts))
  /** recursivly lists file paths */ 
  def ls(path: String)(f: String => Boolean): List[String] = {
    val root = new java.io.File(path)
    (root.isDirectory match {
      case true => (List[String]() /: (root.listFiles.toList map { _.getPath })) ((s, p) => ls(p)(f) ::: s)
      case _ => root.getPath :: Nil
    }).filter(f)
  }
}