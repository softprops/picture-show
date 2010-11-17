package pictureshow

object Server extends Logging {
  import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler}
  import org.eclipse.jetty.util.resource.Resource
  import java.net.URL
  
  object Options {
    val Show = """^--s=(.+)$""".r
    val Port = """^--p=(\d{4})$""".r
    def apply(args: Array[String]) =
      (("example", 3000) /: args)({ (opts, arg) => 
          arg match {
            case Show(showPath) => (showPath, opts._2)
            case Port(port) => (opts._1, Integer.parseInt(port))
            case _ => opts
          }
      })
  }
  
  def main(args: Array[String]) {
    
    val (showPath, port) = Options(args)
    val show = new java.io.File(showPath)
    
    if (show.exists && show.isDirectory) {
      if(!new java.io.File(show, "conf.js").exists) {
        err("conf.js not found under @ `%s`." format show.toString)
      }
      val projector = new Projector(show.toURL)
      if(projector.sections.isEmpty) {
        err("Show content not found @ `%s`. conf.js" format show.toString)
      }
      log("starting show \"%s\" @ \"%s\" on port %s" format(projector.showTitle, show, port))
      unfiltered.jetty.Http(port)  
        .context("/lib") { _.resources(new URL(getClass.getResource("js/show.js"), "..")) }
        .resources(show.toURL)
        .filter(projector)
        .run
    } else err("The path `%s` is not an accessible directory" format show.toString)
  }
  
  def err(msg: String) = {
    System.err.println(msg)
    System.exit(1)
  }
}
