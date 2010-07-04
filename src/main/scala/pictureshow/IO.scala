package pictureshow

trait IO { self: Resolver =>
  /** s/sort/sortWith in scala 2.8 */
  def loadContent(section: String) = paths(section, ".md") sort(_<_)
  def loadJs(section: String) = paths(section, ".js")
  def loadCss(section: String) = paths(section, ".css")
  private def paths(section: String, ext: String): List[String] = 
    paths(section) { _.endsWith(ext) }
  private def paths(section: String)(f: String => Boolean) = 
    Files.ls(Files.path(loadPath :: section :: Nil))(f)
}