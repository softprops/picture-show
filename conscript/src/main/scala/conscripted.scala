package pictureshow

object Main extends PictureShowScript {
  def main(args: Array[String]) {
    startShow(args)
  }
}

class PictureShowScript extends xsbti.AppMain {

  /**
   * --offline
   * --server
   */
  object Options {
    /** which mode to use */
    val Server = "server"
    val Offline = "offline"
    val ServerOpt = """^--(\w)?(%s)""".format(Server).r
    val OfflineOpt = """^--(\w)?(%s)""".format(Offline).r
    def apply(args: Array[String]) =
      (Server /: args)({ (mode, arg) =>
        arg match {
          case ServerOpt(_, s) => s
          case OfflineOpt(_, o) => o
          case _ => mode
        }
      })
  }

  case class Exit(val code: Int) extends xsbti.Exit

  def startShow(args: Array[String]) =
    Options(args) match {
      case Options.Server =>
        Server.instance(args).fold({ errs =>
          println(errs)
          Exit(1)
        }, { svr =>
          svr.run({
            _ => ()
          },{
            _ => println("thank you for watching")
          })
          Exit(0)
        })
      case Options.Offline =>
        offline.Main.instance(args).fold({ errs =>
          println(errs)
          Exit(1)
        }, { ol =>
          ol()
          Exit(0)
        })
    }

  def run(config: xsbti.AppConfiguration) =
    startShow(config.arguments)
}
