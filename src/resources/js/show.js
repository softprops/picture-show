(function($) {
  var util = (function() {
    return {
      dimensions: function() {
        return {
          width: $(window).width(),
          height: $(window).height()
        };
      }
    };
  })();
  
  var slides = (function() {
    return {
      all: function() { 
        return $('#slides .content'); 
      },
      index: function() { 
        return Number(document.location.hash.split('#')[1]); 
      },
      setIndex: function(i) {
        var newSlide = '#slide-' + i;
         if ($(newSlide).size() < 1) { return false; }
         document.location.hash = '#' + i;
         adjustSlides();
      }
    }
  })();

 var setSlideDimensions = function() {
   var d = util.dimensions();
   $('#slides').height(d.height).width(d.width);
   slides.all().height(d.height).width(d.width);
 }

 var showCurrentSlide = function() {
   var dimensions = util.dimensions();
   var index = slides.index();
   var offset = (index || 0) * dimensions.width;

   $('#track').animate({ marginLeft: '-' + offset + 'px' }, 200);
 }

 var verticalAlign = function() {
   var dimensions = util.dimensions();
   var margin = (dimensions.height - $(this).height()) / 2;
   $(this).css({ paddingTop: margin + 'px' });
 }

 var adjustSlides = function() {
   setSlideDimensions();
   showCurrentSlide();
 }

 var move = function(e) {
   
   var DIRECTIONS = {
     37: -1,     // ARROW LEFT
     39: 1,      // ARROW RIGHT
     32: 1,      // SPACE BAR
     13: 1,      // RETURN
     27: 'home', // ESCAPE
     left: -1,
     right: 1
   }

   if (dir = DIRECTIONS[e.which || e]) {
     if (dir == 'home') {
       e.preventDefault();
       e.stopPropagation();
       location.href = '/';
     } else {
       $('#instructions').slideUp(100);
       slides.setIndex(slides.index() + dir);
     }
   }
 }

 function clickMove(e) {
   if (e.pageX < ($(window).width() / 2)) {
     move('left');
   } else {
     move('right');
   }
 }


 function hideInstructions() {
   $('#instructions').slideUp(200);
 }

 $(window).bind('resize', function() { adjustSlides(); });
 
 $(document).bind('keydown', move);
 
 $(document).bind('click', clickMove);
 
 $(document).ready(function() {
   
   slides.setIndex(slides.index() || 0);
   
   if (document.location.search.indexOf('notes') == 1) {
     $('.notes').show();
   }

  // window.setTimeout(hideInstructions, 3000);
  });
})(jQuery);