package pictureshow.offline

object Main extends Logging {
  import java.io.{File => JFile}
  object Options {
    /** path to show */
    val Show = """^--s=(.+)$""".r
    /** where to output results */
    val Out = """^--o=(\d{4})$""".r
    /** resolves env var SHOW_HOME used as a fall back path for shows */
    val ShowHome = System.getenv("SHOW_HOME") match {
      case null => ""
      case str => str
    }
    /** parses options tuple (Show, Port) */
    def apply(args: Array[String]) =
      ((".", "out") /: args)({ (opts, arg) =>
        arg match {
          case Show(showPath) => (showPath, opts._2)
          case Out(out) => (opts._1, out)
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
    val (showPath, out) = Options(args)
    val show = Options.tryPath(showPath)

    if (!show.exists || !show.isDirectory) {
      Left("The path `%s` is not an accessible directory" format show.toString)
    } else if(!new java.io.File(show, "conf.js").exists) {
      Left("conf.js not found under @ `%s`." format show.toString)
    } else {
      val js = "show" :: "jquery.min" :: Nil map { "/js/%s.js" format _ }
      val css = "show" :: Nil map { "/css/%s.css" format _ }
      val assets = js ++ css map { p => getClass().getResource(p.format("show")) }
      Right(() => Offline(show.getAbsolutePath, assets.elements, out))
    }
  }

  def main(args: Array[String]) {
    instance(args).fold({ errs =>
      println(errs)
      System.exit(1)
    }, { ol =>
       ol()
    })
  }

}
