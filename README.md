# picture show

make slideshows with markdown

inspired by the likes of showoff and slide down

(still a bit rough around the edges)

## usage

Currently runs under sbts `run` task once the project is set to `PictureShow Server`

    > project PictureShow Server
    Set current project to PictureShow Server 0.1.0-SNAPSHOT
    > run --s=/path/to/show

This will run your show at http://localhost:3000

To run a show on a specific port add the port after the path to the show

    > run --s=/path/to/show --p=1234

This will run your show at http://localhost:1234

Show path resolution can default to a target directory specified in an environment variable called `SHOW_HOME`

    > export SHOW_HOME=/path/to
    > run --s=show

The directory contents of a show are expected to be in the format


    /yourshow
      config.js
      /sectiona
        sectiona.md
      /sectionb
        sectionb.md


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
    !SLIDE

    one

    !SLIDE

    two

    !SLIDE

    three

Slide content is by default expected in the form of markdown which will be transformed into html.

You can also embed images. It's recommended to place them under the section directory of the same markdown file and to use a relative path.

If you have a project with includes a `sectiona` section with a `foo.jpg` file

    /show
       ..
       /sectiona
         sectiona.md
         foo.jpg

In `sectiona.md`, you'll want to reference `foo.jpg` as

     ![foo](sectiona/foo.jpg)

### assets

#### js and css

You can customize your show with css and javascript but adding a `css/custom.css` or `js/custom.js` /yourshow directory. The content will then be added to the shows header.

#### files

TODOC

## why?

I say why not. Slideshows should be relatively portable and should not require proprietary formats to run. Slideshow presentations should be as simple as possible for an audience to understand. Software should be the same way.


## todo

* use a grownup command line parser of options. possibly [scopt](https://github.com/jstrachan/scopt)

doug tangren [softprops] 2010-11
