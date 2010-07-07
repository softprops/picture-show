package pictureshow

object Server {
  import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler}
  import org.eclipse.jetty.util.resource.Resource
  import java.net.URL
  def main(args: Array[String]) {
    val show = new java.io.File(args match {
      case Array(p) => p
      case _ => "example"
    })
    if (show.exists && show.isDirectory) {
      unfiltered.server.Http(3000)  
        .context("/lib") { _.resources(new URL(getClass.getResource("js/show.js"), "..")) }
        .resources(show.toURL)
        .filter(new Projector(show.toURL))
        .run
    } else {
      System.err.println("The path `%s` is not an accessible directory" format show.toString)
      System.exit(1)
    }
  }
}