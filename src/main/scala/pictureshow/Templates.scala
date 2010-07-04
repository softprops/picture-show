package pictureshow

trait Templates { self: Config =>
  /** render stylesheet links */
  def css(sheets: Seq[String]) = collectionOf(sheets) { s =>
    <link rel="stylesheet" type="text/css" href={s}/>
  }
  /** render script tags */
  def js(scripts: Seq[String]) = collectionOf(scripts) { s => 
    <script type="text/javascript" src={s}></script>
  }
  /** render the show */
  def render(slides : xml.NodeBuffer) = default(new xml.NodeBuffer, slides)
  /** render the show with custom header assets (css, js, ...) */
  def render(heads : xml.NodeBuffer, slides: xml.NodeBuffer) = default(heads, slides)
  /** builds a collection of nodes */
  private def collectionOf(c: Seq[String])(f: String => xml.Node) =
    (new xml.NodeBuffer  /: c) ((b, e) => { b &+ f(e) })
  /** default template */
  private def default(heads: xml.NodeBuffer, slides: xml.NodeBuffer) = 
    <html>
      <head>
        <title>{ showTitle }</title>
        <link rel="stylesheet" type="text/css" href="css/show.css" />
        <script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="js/show.js"></script>
        { heads }
      </head>
      <body>
        <div id="instructions">
          arrow &larr; to go left, arrow &rarr; to go right
        </div>
        <div id="slides">
        <div id="track">
          { slides }
        </div> 
       </div>
      </body>
    </html>
}