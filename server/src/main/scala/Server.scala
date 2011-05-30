package pictureshow

object Server extends Logging {
  import java.net.URL
  import java.io.{File => JFile}
  import unfiltered.jetty._

  object Options {
    /** flag for path to show */
    val Show = """^--s=(.+)$""".r
    /** flag for port to listen on */
    val Port = """^--p=(\d{4})$""".r
    /** resolves env var SHOW_HOME used as a fall back path for shows */
    val ShowHome = System.getenv("SHOW_HOME") match {
      case null => ""
      case str => str
    }
    /** parses options tuple (Show, Port) */
    def apply(args: Array[String]) =
      (("example", 3000) /: args)({ (opts, arg) =>
        arg match {
          case Show(showPath) => (showPath, opts._2)
          case Port(port) => (opts._1, Integer.parseInt(port))
          case _ => opts
        }
      })
    /** first tries resolving path then falls back on SHOW_PATH + path */
    def tryPath(path: String) = new JFile(path) match {
      case f if(f.exists && f.isDirectory) => f
      case _ => new JFile(ShowHome, path)
    }
  }

  class Instance(args: Array[String], err: String => Unit) {
    import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler}
    import org.eclipse.jetty.util.resource.Resource

    val (showPath, port) = Options(args)
    val show = Options.tryPath(showPath)

    if (!show.exists || !show.isDirectory) {
      err("The path `%s` is not an accessible directory" format show.toString)
    }

    if(!new java.io.File(show, "conf.js").exists) {
      err("conf.js not found under @ `%s`." format show.toString)
    }

    val projector = new Projector(show.toURI.toURL)
    if(projector.sections.isEmpty) {
      err("Show content not found @ `%s`. conf.js" format show.toString)
    }

    log("starting show \"%s\" @ \"%s\" on port %s" format(projector.showTitle, show, port))

    val svr = unfiltered.jetty.Http(port).context("/assets") {
       _.resources(new URL(getClass.getResource("/js/show.js"), ".."))
    }.resources(show.toURI.toURL).filter(projector)

    def run(afterRun: svr.type => Unit) = svr.run(afterRun)
  }

  def instance(args: Array[String])(err: String => Unit) = new Instance(args, err)

  def main(args: Array[String]) {
    instance(args)(err => {
      System.err.println(err); System.exit(1)
    }).run(_ => ())
  }
}
