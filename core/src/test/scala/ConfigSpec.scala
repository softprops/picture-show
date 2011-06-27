package pictureshow

import org.specs._

object ConfigSpec extends Specification {
  import java.net.URL;
  val loadPath = new URL(getClass getResource("/conf.js"), ".")
  "Configs" should {
    "have a default title and sections" in {
      object EmptyConfig extends Resolver(loadPath) with IO with Logging with Config {
        override def configName = "empty_conf.js"
      }
      EmptyConfig.showTitle must_== "picture show"
      EmptyConfig.sections must beEmpty
    }
    "use a provided title" in {
      object TitledConfig extends Resolver(loadPath) with IO with Logging with Config
      TitledConfig.showTitle must_== "kittens"
    }
    "use a provided list of sections" in {
      object SectionsConfig extends Resolver(loadPath) with IO with Logging with Config
      SectionsConfig.sections must haveTheSameElementsAs("maki" :: "unagi" :: Nil)
    }
    "allow for overidden config file name" in {
      object AnyConfig extends Resolver(loadPath) with IO with Logging with Config {
        override def configName = "any_conf.js"
      }
      AnyConfig.showTitle must_== "any conf"
    }
  }
}
