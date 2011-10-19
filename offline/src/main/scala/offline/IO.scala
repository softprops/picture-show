package pictureshow.offline

object IO {
  import java.io.{
    File,
    InputStream,
    FileOutputStream,
    FileInputStream,
    OutputStreamWriter
  }
  
  type Resource = { def close(): Unit }

  var utf8 = java.nio.charset.Charset.forName("utf8")

  def loan[R <: Resource, A](r: R)(op: R => A) =
    try {
      Right(op(r))
    } catch {
      case e: Exception => Left(e)
    } finally {
      r.close()
    }
    
  def copy(source: String, target: String): Either[Exception, Unit] =
    copy(new FileInputStream(source), new File(target))
  
  def copy(r: InputStream, target: File): Either[Exception, Unit] = {
    target getParentFile() mkdirs()
    loan(new FileOutputStream(target)) { w =>
      loan(r) { r =>
        var byte: Int = -1
        while({ byte = r.read(); byte != -1 })
          w.write(byte)
      }
    }
  }
  
  def write(file: File)(data: String): Either[Exception, Unit] = 
    loan(new OutputStreamWriter(new FileOutputStream(file), utf8)) {
      _ write(data)
    }
}
