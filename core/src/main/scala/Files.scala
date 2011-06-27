package pictureshow

object Files {
  import java.io.{File => JFile}
  /** build path from parts */
  def path(parts: Seq[String]): String = parts map { _.trim } mkString(System.getProperty("file.separator"))
  /** creator from path */
  def apply(path: String): Option[JFile]  = new JFile(path) match {
    case f:JFile if(f.exists) => Some(f)
    case _ => None
  }
  /** creator from parts */
  def apply(parts: Seq[String]): Option[JFile] = apply(path(parts))
  /** recursivly lists file paths */ 
  def ls(path: String)(f: String => Boolean): List[String] = {
    val root = new java.io.File(path)
    (root.isDirectory match {
      case true => (List[String]() /: (root.listFiles.toList map { _.getPath })) ((s, p) => ls(p)(f) ::: s)
      case _ => root.getPath :: Nil
    }).filter(f)
  }
}