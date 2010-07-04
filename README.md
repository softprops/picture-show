# picture show

make slideshows with markdown

inspired by the likes of showoff and slide down

(work in progress. still a bit rough around the edges)

## usage

currently runs under sbts `run` task

    > sbt
    run path/to/show

the directory contents of a show are expected to be in the format


    /yourshow
      config.js
      /sectiona
        some.md
      /sectionb
        someother.md
        

### config

Shows are configurable through a `config.js` file. This file should be in json
format should include a sections key with an array of section names to render and an optional title. From the example above


    > cat config.js
    {
      "title": "some show title"
      "sections": [
        "sectiona",
        "sectionb"
      ]
    }
  
You can leave out a section or rearrange sections but you should provide at least one.

### slides

Slides represent content that is rendered. One slide is rendered at a time and slides are ordered based on the order of configured sections mentioned above.

Slides are generated from the provided markdown (.md) files as denoted with
a `!SLIDE` delimiter.

The example below generates 3 slides.

    > cat some.md
    
    one
    
    !SLIDE
    
    two
    
    !SLIDE
    
    three
    
Slide content is expected in the form of markdown which will be transformed into html.

### assets

#### js and css

You can customize your show with css and javascript but adding .css or .js files anywhere under /yourshow directory. The content will then be added to the shows header.

#### files

TODOC

## why?

I say why not.