package pictureshow

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
    val show = args match {
      case Array(p) => p
      case _ => "show"
    }
    unfiltered.server.Http(3000)  
      .handler(c => assetHandler("/assets", show))
      .handler(c => assetHandler("/js", getClass.getResource("js/show.js").toString))
      .handler(c => assetHandler("/css", getClass.getResource("css/show.css").toString))
      .filter(new Projector(show))
      .start
  }
}