package pictureshow.offline

import pictureshow.{Renderer,Files}

object Offline {
  import java.io.File

  def someFile(parent: File, name: String): Option[String] =
    if(new File(parent, name).exists)
      Some(name)
    else
      None
  /** Yield the string remaining after the last occurrence of a substring. */
  def strAfter(mark: String)(in: String) =
    if(in contains mark)
      Some(in.substring(in.lastIndexOf(mark) + mark.length + 1))
    else
      Some(in)
  /** Common use. */
  def afterPicshow(p: String) = p.split('/').reverse.takeWhile(!_.endsWith(".jar!")).reverse.mkString("/")

  /** URL => File source/target mappings for core assets. */
  def coreAssetMappings(parent: File)(assets: Iterator[java.net.URL]) =
    assets map { u =>
      u -> afterPicshow(u.toString)
    } map { case (u, t) => u -> new File(parent, "assets/" + t) }
  /** String => String source/target mappings for show assets. */
  def showAssetMappings(fromF: File)(toF: File) = {
    val assets = (new Renderer(fromF.toURI.toURL)).sections ++ someFile(fromF, "js") ++ someFile(fromF, "css")
    val assetPaths = assets.map(s => new File(fromF, s).getAbsolutePath)
    assetPaths.flatMap(sp => Files.ls(sp)(_ => true)) map { p =>
      p -> p.replaceFirst("\\Q%s\\E".format(fromF.getAbsolutePath), toF.getAbsolutePath)
    }
  }
  /** Render a show as if it were being delivered over the wire. */
  def renderShow(fromF: File) =
    (new Renderer(fromF.toURI.toURL)).renderDefault

  /** Build an offline version of a show. */
  def apply(from: String, assets: Iterator[java.net.URL], to: String) = {
    val fromF = new File(from)
    val toF = new File(to)
    // core assets
    val coreAssets = coreAssetMappings(toF)(assets)
    // show assets
    val customTargets = showAssetMappings(fromF)(toF)

    /* Here be side-effects! */

    toF.mkdir()
    // main view
    IO.write(new File(toF, "index.html"))(renderShow(fromF))
    coreAssets.foreach {
      case (u, f) => IO.copy(u.openStream(), f)
    }
    customTargets.foreach {
      case (a, b) => IO.copy(a, b)
    }
  }
}
