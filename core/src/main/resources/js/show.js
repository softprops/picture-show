var show = (function(){
  var move = function(e) {
    var DIRECTIONS = {
      37: -1,     // >
      38: -1,     // up
      39: 1,      // <
      40: 1,      // down
      32: 1,      // _
      13: 1,      // return
      27: 'home', // esc
      left: -1,
      right: 1
    };

    if (dir = DIRECTIONS[e.which || e]) {
      if (dir == 'home') {
        e.preventDefault();
        e.stopPropagation();
        location.href = '/';
      } else {
        show.setIndex(show.index() + dir);
      }
    }
  };

  var clickMove = function(e) {
    if (e.pageX < ($(window).width() / 2)) {
      move('left');
    } else {
      move('right');
    }
  }

  var dimensions = function() {
    return {
      width: $(window).width(),
      height: $(window).height()
    };
  };

  var setSlideDimensions = function() {
    var d = dimensions();
    $('#slides').height(d.height).width(d.width);
    show.slides().height(d.height).width(d.width);
  };

  var showCurrentSlide = function() {
    var d = dimensions()
      , index = (show.index() || 0)
      , offset = index * $('#slides').width();
    $('#reel').animate({ marginLeft: '-' + offset + 'px' }, 200);
  };

  var verticalAlign = function() {
    var d = dimensions()
      , margin = (dimensions.height - $(this).height()) / 2;
    $(this).css({ paddingTop: margin + 'px' });
  };

  var adjustSlides = function() {
    setSlideDimensions();
    showCurrentSlide();
  };

  var followLinks = function(e) {
    e.stopPropagation();
    e.preventDefault();
    window.open(e.target.href);
  }

   $(window).bind('resize', function() { adjustSlides(); });

   $(document).bind('keydown', move);

   $(document).bind("click", clickMove);

   return {
      slides: function() {
        return $('#slides .content');
      },
      index: function() {
        return Number(document.location.hash.split('#')[1]);
      },
      setIndex: function(i) {
        var newSlide = '#slide-' + i;
        if ($(newSlide).size() < 1) {
          return false;
        } else {
          document.location.hash = '#' + i;
          adjustSlides();
          $("a").unbind("click").click(followLinks);
        }
      },
      go: function() {
        this.setIndex(this.index() || 0);
      }
    };
  })();

 $(document).ready(function() {
    show.go();
  });
