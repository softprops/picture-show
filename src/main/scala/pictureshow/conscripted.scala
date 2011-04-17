package pictureshow

class PictureShowScript extends xsbti.AppMain {
  case class Exit(val code: Int) extends xsbti.Exit

  def run(config: xsbti.AppConfiguration) = {
    Server.instance(config.arguments)(err => {
      System.err.println(err)
      Exit(1)
    }).run({
      _ => println("thank you for watching")
    })
    Exit(0)
  }
}
