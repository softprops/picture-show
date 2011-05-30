!SLIDE
# Mobilizing <strong>Scala</strong>
## with <span class="an">robots</span>


![android](/hello/android-icon.png)

@[softprops](http://twitter.com/softprops)

!SLIDE

# why <span class="an">android</span>?

!SLIDE

# a jvm platform

* scala can play too

# offline

* you can play <strong>digdug</strong> on the subway

![digdug](/hello/digdug.gif)

!SLIDE

# typical devt tools

* sbt
* emacs

!SLIDE

# android devt tools

* sbt
* emacs
* + <strong>adb</strong>

!SLIDE

# step one: <strong>templatize</strong>

    g8 n8han/android-app

!SLIDE

# step two: <strong>build</strong>

    class Calculator extends Activity {
       def onCreate(instance: Bundle) = {
          super.onCreate(instance)
          //...
       }
    }

!SLIDE

# step three: <strong>deploy</strong>

    sbt reinstall-device

!SLIDE

# step four: <strong>repeat</strong>
# <strong>two</strong> and <strong>three</strong>

!SLIDE

# why <strong>scala</strong>?

 * consice
 * flexible
 * expressive
 * fun

!SLIDE

# but...

* what about <strong>size</strong>?
* what about <strong>compatablity</strong>?

!SLIDE

# but...

* what about <strong>size</strong>?

`let the progaurd be with you`

 ![pacman](/hello/pacman.jpg) `|bytecode.bytecode.bytecode|`

 `|b.c.|`  ![pacman](/hello/pacman.jpg)

* what about <strong>compatability</strong>?

 `leave it to dex`

!SLIDE

# wins

* lazy immutablity
* composability

!SLIDE

# lazy immutability

    public class Okay extends Activity {
      private View view = null;

      @Override
      public void onCreate(Bundle instance) {
        super.onCreate(instance);
        view = findViewById(R.id.my_view);
      }
    }

!SLIDE

# lazy immutability

    class Better extends Activity {
      lazy val view = findViewById(R.id.my_view)

      override def onCreate(instance: Bundle) =
        super.onCreate(instance)
    }

!SLIDE

# composability

    public class A extends Activity {}
    public class B extends A {}
    public class C extends B {}

!SLIDE

# composability

    /** booo */
    public class A extends Activity {}
    public class B extends A {}
    public class C extends B {}
    // damn, now I need another shim in my model!


!SLIDE

# composability

(as reusable behavioral sprinkles)

    trait Shaking ...
    trait Toasted ...
    trait Cropping ...
    trait OAuth ...
    class A extends Activity
      with Shaking with Toasted
      with Cropping with OAuth

!SLIDE

# fails

* <strong>AsyncTask</strong> no worky

Unit*

    new AsyncTask[Unit, Unit, Unit] {
         def doInBackground(params: Unit*) =

Unit...

    new AsyncTask<Unit, Unit, Unit> {
        public void doInBackground(Unit... params) {

!SLIDE

# oh yeah?

    trait Async {
      val handler = new Handler
      def async(f: => Unit) =
        handler.post(new Runnable {
          def run = f
        })
    }

!SLIDE

     class Miner extends Activity with Aysnc {
       def dig = async {
          shovelShovelShovel
        }
     }

!SLIDE

# room for improvement

* scalanization tool kit
* examples
* you!

!SLIDE

# tick!
[github.com/softprops/tick/tree/pictures](https://github.com/softprops/tick/tree/pictures)

## todo

* better name
* fix bitmap write bug
* non-stock icon
* pic pool
* more caching

!SLIDE

# q?
