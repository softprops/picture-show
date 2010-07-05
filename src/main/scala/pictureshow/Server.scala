package pictureshow

object Server {
  import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler}
  import org.eclipse.jetty.util.resource.Resource
  import java.net.URL
  def main(args: Array[String]) {
    def assetHandler(contextPath: String, base: URL) = {
      val files = new ResourceHandler
      files.setBaseResource(Resource.newResource(base))
      val context = new ContextHandler
      context.setContextPath(contextPath)
      context.setAliases(true)
      context.setHandler(files)
      context
    }
    val show = new java.io.File(args match {
      case Array(p) => p
      case _ => "show"
    })
    if (show.exists && show.isDirectory) {
      unfiltered.server.Http(3000)  
        .handler(c => assetHandler("/assets", show.toURL))
        .handler(c => assetHandler("/js", new URL(getClass.getResource("js/show.js"), ".")))
        .handler(c => assetHandler("/css", new URL(getClass.getResource("css/show.css"), ".")))
        .filter(new Projector(show.toURL))
        .run
    } else {
      System.err.println("The path `%s` is not an accessible directory" format show.toString)
      System.exit(1)
    }
  }
}