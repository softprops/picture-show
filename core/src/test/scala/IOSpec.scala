package pictureshow

import org.specs._

object IOSpec extends Specification {
  import java.net.URL;
  val loadPath = new URL(getClass getResource("/conf.js"), ".")

  "IO" should {
    "know when files exist" in {
      object FilesExistingIO extends Resolver(loadPath) with IO
      FilesExistingIO.exists("conf.js") must beTrue
    }
    "know when files don't exist" in {
      object FilesExistingIO extends Resolver(loadPath) with IO
      FilesExistingIO.exists("bogus.txt") must beFalse
    }
    "slurp existing files" in {
      object SlurpingIO extends Resolver(loadPath) with IO
      SlurpingIO.slurp("empty_conf.js") must beSome("{}")

    }
    "not slurp missing files" in {
      object SlurpingIO extends Resolver(loadPath) with IO
      SlurpingIO.slurp("meh.txt") must beNone
    }
  }
}
