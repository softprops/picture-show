package pictureshow

class PictureShowScript extends xsbti.AppMain {

  /**
   * --offline
   * --server
   *
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

  def run(config: xsbti.AppConfiguration) = {
    Options(config.arguments) match {
      case Options.Server =>
        Server.instance(config.arguments)(err => {
          System.err.println(err)
          Exit(1)
        }).run({
          _ => println("thank you for watching")
        })
      case Options.Offline =>
         offline.Main.main(config.arguments)
    }
    Exit(0)
  }
}
