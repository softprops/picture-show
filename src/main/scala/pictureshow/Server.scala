package pictureshow

object Server extends Logging {
  import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler}
  import org.eclipse.jetty.util.resource.Resource
  import java.net.URL
  def main(args: Array[String]) {
    val show = new java.io.File(args match {
      case Array(p) => p
      case _ => "example"
    })
    val port = args match {
      case Array(_, port) => Integer.parseInt(port)
      case _ => 3000
    }
    if (show.exists && show.isDirectory) {
      val projector = new Projector(show.toURL)
      log("starting show \"%s\" @ \"%s\" on port %s" format(projector.showTitle, show, port))
      unfiltered.jetty.Http(port)  
        .context("/lib") { _.resources(new URL(getClass.getResource("js/show.js"), "..")) }
        .resources(show.toURL)
        .filter(projector)
        .run
    } else {
      System.err.println("The path `%s` is not an accessible directory" format show.toString)
      System.exit(1)
    }
  }
}
