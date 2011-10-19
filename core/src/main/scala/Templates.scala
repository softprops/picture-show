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
    default(heads, slides, bodyScripts)
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
        <link rel="stylesheet" type="text/css" href="assets/css/show.css" />
        <link rel="stylesheet" type="text/css" href="assets/css/prettify.css" />
        <script type="text/javascript" src="assets/js/jquery.min.js"></script>
        <script type="text/javascript" src="assets/js/show.js"></script>
        <script type="text/javascript" src="assets/js/prettify/prettify.js"></script>
        {
          ("apollo" :: "css" :: "hs" :: "lisp" :: "lua" :: "ml" :: "proto" ::
          "scala" :: "sql" :: "sql" :: "vb" :: "vhdl" :: "wiki" :: 
          "yaml" :: Nil).map { lang =>
            <script type="text/javascript" src={
              "assets/js/prettify/lang-%s.js".format(lang)
            }></script>
          } ++ heads
        }
      <script type="text/javascript"><!--
        window.onload=function() { prettyPrint(); };
      --></script>
      </head>
      <body>
        <div id="slides">
          <div id="reel">
            { slides }
          </div>
        </div>
        {bodyScripts}
      </body>
    </html>
}
