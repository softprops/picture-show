package pictureshow

object Uris {
  import org.apache.commons.codec.binary.Base64.encodeBase64
  import java.io.{ByteArrayOutputStream, File, FileInputStream, InputStream}

  def dataUri(f: File, mime: String) = {
    use(new FileInputStream(f)) { in =>
       def consume(in: InputStream, out: ByteArrayOutputStream, buf: Array[Byte]): Array[Byte] = {
          in.read(buf) match {
            case -1 => out.toByteArray
            case num =>
              out.write(buf, 0, num)
              consume(in, out, buf)
          }
       }
       val data = new String(encodeBase64(consume(in, new ByteArrayOutputStream(), new Array[Byte](1024))), "utf-8")
       "data:%s;base64,%s" format (mime, data)
    }
  }

  def use[C <: { def close(): Unit }, T](c: C)(f: C => T): T =
    try { f(c) }
    finally { c.close() }
}
