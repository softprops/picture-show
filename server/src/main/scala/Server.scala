package pictureshow

object Server extends Logging {
  import java.net.URL
  import java.io.{File => JFile}
  import unfiltered.jetty

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
      ((".", 3000) /: args)({ (opts, arg) =>
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

  def instance(args: Array[String]) = {
    val (showPath, port) = Options(args)
    val show = Options.tryPath(showPath).getCanonicalFile

    if (!show.exists || !show.isDirectory) {
      Left("The path `%s` is not an accessible directory" format show.toString)
    } else if (!new JFile(show, "conf.js").exists) {
      Left("conf.js not found under @ `%s`." format show.toString)
    } else {
      val projector = new Projector(show.toURI.toURL)
      if (projector.sections.isEmpty) {
        Left("Show content not found @ `%s`. conf.js" format show.toString)
      } else {
        log("starting show \"%s\" @ \"%s\" on port %s" format(projector.showTitle, show, port))
        Right(jetty.Http(port).context("/assets") {
          _.resources(new URL(getClass.getResource("/js/show.js"), ".."))
        }.resources(show.toURI.toURL).filter(projector))
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
