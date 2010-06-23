package pictureshow

import unfiltered._
import unfiltered.request._
import unfiltered.response._

class Projector extends Config with Resolver with IO with Markup with Templates with unfiltered.Plan {
  /** by default, use all sections */
  def sections = "" :: Nil
  
  def filter = {
    case GET(Path("/", _)) => ResponseString(render(mkSlides).toString)
  }
}

object PictureShowServer extends Config {
  import org.eclipse.jetty.server.{Server => JettyServer, Connector, Handler}
  import org.eclipse.jetty.server.handler.{HandlerCollection, ResourceHandler, ContextHandler}
  import org.eclipse.jetty.servlet.{FilterHolder, ServletContextHandler}
  import org.eclipse.jetty.server.bio.SocketConnector
  import org.eclipse.jetty.util.resource.Resource
  val showURL = new java.io.File("show").toURI.toURL
  println("resrcs " + showURL)
  def main(args: Array[String]) {
    
    System.setProperty("org.eclipse.jetty.util.log.DEBUG", "true")
    val server = unfiltered.server.Http(3000).filter(new Projector)
      //.resources(getClass.getResource("js/show.js"))
      //.resources(getClass.getResource("css/show.css"))
    
    // Let me make a context to handle the following images.
    //val context = new ContextHandler
    //context.setVirtualHosts(Array("images.localhost"));
    // the resources are rooted at / in the above virtual host.
    //context.setContextPath("/")
    // set where the actual files are (may not be needed)
    //context.setResourceBase(resourceBase)
    
    val files = new ResourceHandler
    files.setResourceBase(".")
    println("files " + files.getResourceBase)
    //server.handlers.addHandler(new org.eclipse.jetty.handler.RequestLogHandler)
    server.handler(c => files)
    server.start
  }
}

trait Config {
  def resourceBase = "show"
  def showTitle = "picture show"
}

trait Templates { self: Config =>
  
  /** render the show */
  def render(slides: xml.NodeBuffer) = default(slides)
  
  private def default(slides: xml.NodeBuffer) = 
    <html>
      <head>
        <title>{showTitle}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
        <stylesheet type="text/css" href="css/show.css" />
        <script type="text/javascript" src="js/show.js"></script>
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
  /** path to root of slides*/
  def loadPath = ""
}

trait Markup { self: IO =>
  import com.tristanhunt.knockoff.DefaultDiscounter._
  
  def mkSlides = {
    val sections = List(".")
    (new xml.NodeBuffer /: sections)((l, s) => {
      val files = loadFiles(s)
      println(files)
      l &+ ((new scala.xml.NodeBuffer  /: files)((m, f) => {
        m &+ md("show/" + f)
      }))
    })
  }
  
  def md(fname: String) = {
    println("fname %s" format fname)
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
  def loadFiles(section: String) = {
    /** s/sort/sortWith in scala 2.8 */
    Files.ls(section) { _.endsWith(".md") }.toList.sort(_<_)
  }
}

object Files { //self: Resolver =>
 /** creator */
 def apply(path: String) = new java.io.File("show", path)
 /** lists file paths */
 def ls(path: String)(f: String => Boolean) = {
   val sec = new java.io.File("show", path)
   println(sec.list)
   (sec.isDirectory match {
     case true => sec.list.toSeq
     case _ => sec.getAbsolutePath :: Nil
   }).filter(f)
 }
}