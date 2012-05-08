# picture show

make slideshows with markdown text.

requirements:

- a text editor
- [conscript][cs]

## Install

Run as a [conscript][cs] program

    cs softprops/picture-show

This will will install a program called `pshow` in your `~/bin` directory. Ensure this is your path.

## Usage

Once installed you can serve a picture show that exists in the current directory with

    pshow

After validating a file called `conf.js` at the root of your presentation, your slides will be available for viewing by pointing your browser to `http://localhost:3000`

To service a specific show or host on a specific port use the `s` and or `p` command line flags which represent the absolute show path and port respectively

    pshow -s=/path/to/show -p=1234

This will serve your show located at `/path/to/show` at `http://localhost:1234`

Show path resolution can default to a target directory specified in an environment variable called `SHOW_HOME`

    export SHOW_HOME=/path/to
    pshow -s=show

This will serve your show located at `/path/to/show` at `http://localhost:3000`

On the go? You can export a show as a static html file that can run anywhere. Just add the flag `offline`

    pshow --offline

This will generate the static contents of your show in a directory relative to your current one called `out`

To out the produced files to a target directory, add the `o` flag

    pshow --offline -o=/path/to/output

The directory contents of a show are expected to be in the format

    /yourshow
      conf.js
      /sectiona
        sectiona.md
      /sectionb
        sectionb.md

Gisting your picture show

You can also host your picture show as gist on [github](http://gist.github.com) by specficying the gist by url using the `g` flag.

     pshow -g=https://gist.github.com/asdfasdf
     
Gists only support a flat file structure. Simply flatten the structure described above and picture show will take care of the rest.


### conf-iguration

Shows are configurable through a `conf.js` file. This file should be in json
format should include a sections key with an array of section names to render and an optional title. From the example above


    > cat conf.js
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

Slide content is, by default, expected in the form of markdown which will be transformed into html.

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

You can customize your show with css and javascript by adding a `css/custom.css` or `js/custom.js` /yourshow directory. The content will then be added to the shows header.

#### files

If you want to embed an image or or other resource in your slides. Use the path from the root of your show

     /show
        /foo
           bar.jpg
           bar.md

    cat show/foo/bar.md
    #SLIDE!

    ![bar](foo/bar.jpg "bar")

## giter8

An example [giter8](http://github.com/n8han/giter8#readme) template is available via

    g8 softprops/picture-show

## why?

I say why not. Slideshows should be relatively portable and should not require proprietary formats to run. Slideshow presentations should be as simple as possible for an audience to understand. Software should be the same way.

## todo

* use a grownup command line parser of options. possibly [scopt](https://github.com/jstrachan/scopt)
* supported exporting a show to pdf

doug tangren [softprops] 2010-12

[cs]: https://github.com/n8han/conscript#readme
