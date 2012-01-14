package pictureshow

object Server extends Logging {
  import java.net.URL
  import java.net.URI
  import java.io.{ File => JFile }
  import unfiltered.jetty.{ Http, Server }

  object Options {
    /** flag for path to show */
    val Show = """^-s=(.+)$""".r
    /** flag for port to listen on */
    val Port = """^-p=(\d{4})$""".r
    val Gist = """^-g=(.*)""".r
    /** resolves env var SHOW_HOME used as a fall back path for shows */
    val ShowHome = System.getenv("SHOW_HOME") match {
      case null => ""
      case str => str
    }

    /** parses options tuple (Show, Port) */
    def apply(args: Array[String]) =
      ((".", 3000, (None: Option[URI])) /: args)({ (opts, arg) =>
        arg match {
          case Show(showPath) => (showPath, opts._2, opts._3)
          case Port(port)     => (opts._1, Integer.parseInt(port), opts._3)
          case Gist(uri)      => (opts._1, opts._2, tryUri(uri))
          case _ => opts
        }
      })

    /** first tries resolving path then falls back on SHOW_PATH + path */
    def tryPath(path: String) = new JFile(path) match {
      case f if(f.exists && f.isDirectory) => f
      case _ => new JFile(ShowHome, path)
    }

    def tryUri(uri: String) = try {
      Some(new URI(uri))
    } catch {
      case _ => None
    }
  }

  def instance(args: Array[String]) = {
    val (showPath, port, gist) = Options(args)
    
    def serve[T](f: Server => T): T =
      f(Http(port).context("/assets") {
        _.resources(new URL(getClass.getResource("/js/show.js"), ".."))
      })

    gist match {
      case Some(uri) =>
        val p = new Projector(uri)
        if(p.sections.isEmpty) Left("Empty show sections")
        else {
          log("starting show \"%s\" @ \"%s\" on port %s" format(p.showTitle, uri, port))
          Right(serve { _.filter(p) })
        }
      case _ =>
        val show = Options.tryPath(showPath).getCanonicalFile
        if (!show.exists || !show.isDirectory) {
          Left("The path `%s` is not an accessible directory" format show)
        } else if (!new JFile(show, "conf.js").exists) {
          Left("conf.js not found under @ `%s`." format show)
        } else {
          val p = new Projector(show.toURI)
          if (p.sections.isEmpty) Left("Empty show sections in %s conf.js" format show)
          else {
            log("starting show \"%s\" @ \"%s\" on port %s" format(
              p.showTitle, show, port))
            Right(serve { _.resources(show.toURI.toURL).filter(p) })
          }
       }
    }
  }

  def main(args: Array[String]) {
    instance(args).fold({ err =>
      println("instance failed!")
      println(err)
      System.exit(1)
    }, { svr =>
      svr.run(_ => ())
    })
  }

}
