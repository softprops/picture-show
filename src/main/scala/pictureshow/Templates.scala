package pictureshow

trait Templates { self: Config =>
  /** render stylesheet links */
  def css(sheets: Seq[String]) = collectionOf(sheets) { s =>
    <link rel="stylesheet" type="text/css" href={s+"?"+System.currentTimeMillis}/>
  }
  /** render script tags */
  def js(scripts: Seq[String]) = collectionOf(scripts) { s =>
    <script type="text/javascript" src={s+"?"+System.currentTimeMillis}></script>
  }
  /** render the show */
  def render(slides : xml.NodeBuffer) = default(new xml.NodeBuffer, slides, new xml.NodeBuffer)
  /** render the show with custom header assets (css, js, ...)
   * @note in scala 2.8 you don't need the last to args
   */
  def render(heads : xml.NodeBuffer, slides: xml.NodeBuffer, bodyScripts: xml.NodeBuffer) = xml.Xhtml.toXhtml(
    default(heads, slides, bodyScripts), false, false
  )
  /** builds a collection of nodes */
  private def collectionOf(c: Seq[String])(f: String => xml.Node) =
    (new xml.NodeBuffer  /: c) ((b, e) => { b &+ f(e) })
  /** default template */
  private def default(heads: xml.NodeBuffer, slides: xml.NodeBuffer, bodyScripts:xml.NodeBuffer) =
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <title>{ showTitle }</title>
        <link rel="stylesheet" type="text/css" href="lib/css/show.css" />
        <script type="text/javascript" src="lib/js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="lib/js/show.js"></script>
        { heads }
      </head>
      <body>
        <div id="instructions">
          arrow &larr; to go left, arrow &rarr; to go right
        </div>
        <div id="slides">
        <div id="reel">
          { slides }
        </div>
       </div>
       {bodyScripts}
      </body>
    </html>
}
