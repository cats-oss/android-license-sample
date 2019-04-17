package com.github.aakira.license

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.github.aakira.license.view.ObservableScrollView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Random
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

  companion object {
    //// speed factor ////
    private const val FACTOR_NORMAL = 1f
    private const val FACTOR_SLOW = 0.9f
    private const val FACTOR_SLOWER = 0.6f
    private const val FACTOR_SLOWEST = 0.2f

    //// scrolling amount per scene ////
    private const val FIRST_SCENE = 1000f   // immutable
    // second scene is mutable
    // third scene is mutable
    private const val FOURTH_SCENE = 2000f  // immutable
    private const val FIFTH_SCENE = 8000f   // immutable
    private const val SIXTH_SCENE = 5000f   // immutable
    private const val PER_SCENE_COUNT = 2   // number of mutable scenes : scene2, scene3

    //// objects
    private const val FIRST_ROAD_SCALE = 2f // scale of road at first
    private const val BICYCLE_SCALE = 0.5f  // scale of bicycle when moving in the middle
    private const val FIRST_ROAD_SCALE_FACTOR = 0.2f // factor of road scale in the first scene
    private const val SECOND_ROAD_OFFSET = (FIRST_SCENE - FIRST_SCENE * FIRST_ROAD_SCALE_FACTOR).toInt()
    private const val MOON_ROTATION_ANGLE = 50f

    private const val OBJECT_AMOUNT = 100 // number of object
    // object offset : distance from the center (xOffset)
    private const val WOOD_OFFSET = 0f
    private const val BUILDING_OFFSET = 100f
    private const val TOWER_OFFSET = 250f
    // object elevation : z index
    private const val ELEVATION_FRONT = 30f
    private const val ELEVATION_CENTER = 20f
    private const val ELEVATION_BACK = 10f
  }

  //// scrolling amount per scene ////
  private var SECOND_SCENE = 0f // mutable
  private var THIRD_SCENE = 0f  // mutable

  //// criterion values ////
  private var screenWidth: Int by Delegates.notNull()
  private var screenHeight: Int by Delegates.notNull()
  private var baseHeight: Int by Delegates.notNull()  // half of the screen is criterion
  private var baseWidth: Int by Delegates.notNull()   // half of the screen is criterion
  private var scrollingHeight: Float by Delegates.notNull() // overall scrolling amount
  private var bicycleDy: Int by Delegates.notNull()   // scrolling amount of the bicycle

  //// objects size ////
  private val TOWER_WIDTH: Int by lazy { (100 * resources.displayMetrics.density).toInt() }
  private val TOWER_HEIGHT: Int by lazy { (319 * resources.displayMetrics.density).toInt() }
  private val WOOD_WIDTH: Int by lazy { (100 * resources.displayMetrics.density).toInt() }
  private val WOOD_HEIGHT: Int by lazy { (130 * resources.displayMetrics.density).toInt() }
  private val CHURCH_WIDTH: Int by lazy { (100 * resources.displayMetrics.density).toInt() }
  private val CHURCH_HEIGHT: Int by lazy { (200 * resources.displayMetrics.density).toInt() }
  private val HOME_WIDTH: Int by lazy { (100 * resources.displayMetrics.density).toInt() }
  private val HOME_HEIGHT: Int by lazy { (150 * resources.displayMetrics.density).toInt() }

  //// objects list ////
  private val towers: Array<MovingObject?> = arrayOfNulls<MovingObject?>(OBJECT_AMOUNT)
  private val objects: Array<MovingObject?> = arrayOfNulls(OBJECT_AMOUNT)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // read license and credit text from markdown
    licenseText.text = readMarkdown(R.raw.license)
    creditText.text = readMarkdown(R.raw.credit)

    //// add moving objects
    val rand = Random()
    for (i in 0 until OBJECT_AMOUNT step 2) {

      // add tower objects
      towers[i] = MovingObject(
        rootView.addObject(TOWER_WIDTH, TOWER_HEIGHT, R.drawable.ic_tower_left, Direction.LEFT, ELEVATION_BACK),
        Direction.LEFT, TOWER_OFFSET, FACTOR_SLOWEST
      )
      towers[i + 1] = MovingObject(
        rootView.addObject(TOWER_WIDTH, TOWER_HEIGHT, R.drawable.ic_tower_right, Direction.RIGHT, ELEVATION_BACK),
        Direction.RIGHT, TOWER_OFFSET, FACTOR_SLOWEST
      )

      // add building and wood objects at random
      val pattern = rand.nextInt(10) // weighting
      val objectWidth: Int
      val objectHeight: Int
      val drawableLeft: Int
      val drawableRight: Int
      val elevation: Float
      val offset: Float
      val factor: Float

      when (pattern) {
        0 -> {
          objectWidth = CHURCH_WIDTH
          objectHeight = CHURCH_HEIGHT
          drawableLeft = R.drawable.ic_church
          drawableRight = R.drawable.ic_church
          elevation = ELEVATION_CENTER
          offset = BUILDING_OFFSET
          factor = FACTOR_SLOWER
        }
        1, 2, 3 -> {
          objectWidth = HOME_WIDTH
          objectHeight = HOME_HEIGHT
          drawableLeft = R.drawable.ic_house_left
          drawableRight = R.drawable.ic_house_right
          elevation = ELEVATION_CENTER
          offset = BUILDING_OFFSET
          factor = FACTOR_SLOWER
        }
        else -> { // 4~
          objectWidth = WOOD_WIDTH
          objectHeight = WOOD_HEIGHT
          drawableLeft = R.drawable.ic_wood
          drawableRight = R.drawable.ic_wood
          elevation = ELEVATION_FRONT
          offset = WOOD_OFFSET
          factor = FACTOR_NORMAL
        }
      }
      objects[i] = MovingObject(
        rootView.addObject(objectWidth, objectHeight, drawableLeft, Direction.LEFT, elevation),
        Direction.LEFT, offset, factor
      )
      objects[i + 1] = MovingObject(
        rootView.addObject(objectWidth, objectHeight, drawableRight, Direction.RIGHT, elevation),
        Direction.RIGHT, offset, factor
      )
    }

    // Raise the hierarchy than objects created later
    ViewCompat.setTranslationZ(observableScrollView, Float.MAX_VALUE)

    //// Wait for attached views
    rootView.viewTreeObserver.addOnGlobalLayoutListener(
      object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
          rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)

          // set screen size
          screenWidth = rootView.width
          screenHeight = rootView.height
          baseHeight = screenHeight / 2
          baseWidth = screenWidth / 2
          bicycleDy = baseHeight / 4

          // add padding to license text
          val licensePadding = screenHeight + FIRST_SCENE
          licenseText.setPadding(0, licensePadding.toInt(), 0, 0)
          val creditPadding = screenHeight * 3
          creditText.setPadding(0, creditPadding, 0, 0)

          // calculate scrolling amount
          scrollingHeight = observableScrollView.getChildAt(0).height - observableScrollView.height +
            licensePadding + creditPadding

          // adjust size of mutable scene
          val perSceneSize = (scrollingHeight - FIRST_SCENE - FOURTH_SCENE - FIFTH_SCENE - SIXTH_SCENE) / PER_SCENE_COUNT
          SECOND_SCENE = perSceneSize
          THIRD_SCENE = perSceneSize

          // init views
          roadView.scaleX = FIRST_ROAD_SCALE
          sunView.translationY = 200f  // hide the sun behind the mountain
          moonView.translationY = 200f // hide the moon behind the mountain
          nightView.translationY = baseHeight.toFloat()

          // calculate interval each object
          val mutableSceneSize = SECOND_SCENE + THIRD_SCENE - screenHeight
          val towerInterval = mutableSceneSize / OBJECT_AMOUNT
          val pattern = 2 // weighting
          val interval1 = mutableSceneSize / (pattern * OBJECT_AMOUNT)
          val interval2 = interval1 - 50f // 50 is offset

          for (i in 0 until OBJECT_AMOUNT step 2) {
            (i * towerInterval).let {
              towers[i]?.startOffset = it
              towers[i + 1]?.startOffset = it
            }
            (rand.nextInt(pattern) + 1).let {
              objects[i]?.startOffset = i * interval1 * it
              objects[i + 1]?.startOffset = i * interval2 * it
            }
          }
        }
      })

    //// observe scroll position
    observableScrollView.scrollListener = object : ObservableScrollView.ScrollListener {
      override fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {

        // =========First scene=========
        val firstFactor = Math.min(1f, Math.max(0f, 1 - ((FIRST_SCENE - y) / FIRST_SCENE)))

        // =========second scene=========
        val secondPosition = Math.max(0f, y - FIRST_SCENE)
        val secondFactor = Math.min(1f, Math.max(0f, 1 - ((SECOND_SCENE - secondPosition) / SECOND_SCENE)))

        // sun
        val sunRadius = baseWidth - (sunView.width / 2)
        val sunDegree = Math.max(0.0, Math.min(200.0, 200.0 * secondFactor)) - 10 // -10°~190°
        sunView.translationX = -sunRadius * Math.cos(sunDegree * Math.PI / 180.0).toFloat()
        sunView.translationY = -sunRadius * Math.sin(sunDegree * Math.PI / 180.0).toFloat()

        // objects
        for (i in 0 until OBJECT_AMOUNT) {
          // towers
          towers[i]?.run {
            view.scroll(direction, Math.max(0f, secondPosition - startOffset), xOffset, baseWidth, factor)
          }
          // random objects
          objects[i]?.run {
            view.scroll(direction, Math.max(0f, secondPosition - startOffset), xOffset, baseWidth, factor)
          }
        }

        // =========third scene=========
        val thirdPosition = Math.max(0f, y - SECOND_SCENE - FIRST_SCENE)
        val thirdFactor = Math.min(1f, Math.max(0f, 1 - ((THIRD_SCENE - thirdPosition) / THIRD_SCENE)))

        // background
        skyView2.translationY = -baseHeight * thirdFactor

        // moon
        val moonRadius = baseWidth - (moonView.width / 2)
        val moonDegree: Double = Math.max(0.0, Math.min(200.0, 200.0 * thirdFactor)) - 10 // -10° ~ 190°
        moonView.translationX = -moonRadius * Math.cos(moonDegree * Math.PI / 180.0).toFloat()
        moonView.translationY = -moonRadius * Math.sin(moonDegree * Math.PI / 180.0).toFloat()
        // rotate the moon for the direction of the sun
        moonView.rotation = Math.max(0f, Math.min(MOON_ROTATION_ANGLE, MOON_ROTATION_ANGLE * thirdFactor))

        // =========fourth scene=========
        val fourthPosition = Math.max(0f, y - THIRD_SCENE - SECOND_SCENE - FIRST_SCENE)
        val fourthFactor = Math.min(1f, Math.max(0f, 1 - ((FOURTH_SCENE - fourthPosition) / FOURTH_SCENE)))

        // background
        nightView.translationY = Math.min(baseHeight.toFloat(), Math.max(0f, baseHeight - baseHeight * fourthFactor))

        // =========fifth scene=========
        val fifthPosition = Math.max(0f, y - FOURTH_SCENE - THIRD_SCENE - SECOND_SCENE - FIRST_SCENE)
        val fifthFactor = Math.min(1f, Math.max(0f, 1 - ((FIFTH_SCENE - fifthPosition) / FIFTH_SCENE)))

        // tilt up
        roadView.translationY = baseHeight * fifthFactor
        groundView.translationY = baseHeight * fifthFactor
        // mountain moving is written in sixth scene

        // =========sixth scene=========
        val sixthPosition = Math.max(0f, y - FIFTH_SCENE - FOURTH_SCENE - THIRD_SCENE - SECOND_SCENE - FIRST_SCENE)
        val sixthFactor = Math.min(1f, Math.max(0f, 1 - ((SIXTH_SCENE - sixthPosition) / SIXTH_SCENE)))

        // tilt up : mountain
        mountainBackView.translationY = baseHeight * fifthFactor + sixthPosition
        mountainFrontView.translationY = baseHeight * fifthFactor * FACTOR_SLOW + sixthPosition

        // scale in the logo
        logoView.scaleX = sixthFactor
        logoView.scaleY = sixthFactor

        // =========plural  scene=========
        val bicycleScale = when {
          fourthPosition > 0 -> 1 - (BICYCLE_SCALE + BICYCLE_SCALE * fourthFactor)
          else -> 1 - (BICYCLE_SCALE * firstFactor)
        }
        bicycleView.scaleX = bicycleScale
        bicycleView.scaleY = bicycleScale
        bicycleView.translationY = when {
          fourthPosition > 0 -> -bicycleDy - (baseHeight - bicycleDy - bicycleView.height / 2) * fourthFactor
          else -> -bicycleDy * firstFactor // first scene
        }

        // bicycle view
        if (secondPosition > 0) bicycleView.position(y)

        // mountain scale
        if (fourthPosition <= 0) {
          (1 + y / (scrollingHeight - FIFTH_SCENE)).let {
            mountainFrontView.scaleX = it
            mountainFrontView.scaleY = it
          }
          (1 + (y / (scrollingHeight - FIFTH_SCENE)) * FACTOR_SLOWER).let {
            mountainBackView.scaleX = it
            mountainBackView.scaleY = it
          }
        }

        // road view
        when {
          fourthPosition > 0 -> {
            // fifth scene ~
            // widen the width of the road to make it looks like moved the camera angle upwards
            roadView.scaleX = 1 + fifthFactor * 4
          }
          secondPosition > 0 -> {
            // second scene, third scene
            roadView.position(y, SECOND_ROAD_OFFSET)
          }
          else -> {
            // first scene
            roadView.position((-y * FIRST_ROAD_SCALE_FACTOR).toInt()) // scale out is slow
            // reduce the width of the road to make it looks like pulled the camera angle
            roadView.scaleX = 1 + Math.max(0f, FIRST_ROAD_SCALE - 1f - firstFactor)
          }
        }
      }
    }
  }

  /**
   * direction left or right
   * xOffset 0~baseWidth, offset of horizontal. the lager it, the farther you look
   * factor speed of movement
   * startOffset 0~SCENE_SIZE, offset until object starts moving from scene
   */
  private data class MovingObject(
    val view: View,
    val direction: Direction,
    val xOffset: Float,
    val factor: Float,
    var startOffset: Float = 0f
  )

  private enum class Direction {
    LEFT,
    RIGHT
  }

  private fun View.scroll(direction: Direction, position: Float, offset: Float, dx: Int, factor: Float = 1f) {
    if (direction == Direction.LEFT) scrollLeft(position, offset, dx, factor)
    else scrollRight(position, offset, dx, factor)
  }

  /**
   * pivot x,y is lower right
   *
   * position scrollのposition(y)
   * offset left direction is minus, right direction is plus
   * dx total movement distance f x axis
   * factor 0f-1.0f, adjust moving speed
   */
  private fun View.scrollLeft(position: Float, offset: Float, dx: Int, factor: Float = 1f) {
    translationX = -position * factor - offset
    translationY = position * factor

    Math.max(0f, Math.min(1f, (x + width) / (dx - offset))).let {
      scaleX = 1 - it
      scaleY = 1 - it
    }
  }

  /**
   * pivot x,y is lower left
   *
   * position scrollのposition(y)
   * offset left direction is minus, right direction is plus
   * dx total movement distance f x axis
   * factor 0f-1.0f, adjust moving speed
   */
  private fun View.scrollRight(position: Float, offset: Float, dx: Int, factor: Float = 1f) {
    translationX = position * factor + offset
    translationY = position * factor

    Math.max(0f, Math.min(1f, (x - offset - dx) / (dx - offset))).let {
      scaleX = it
      scaleY = it
    }
  }

  /**
   * add views to root layout.
   */
  private fun ConstraintLayout.addObject(
    width: Int, height: Int, @DrawableRes imageId: Int, direction: Direction, elevation: Float
  ): View = AppCompatImageView(context).also {
    it.setImageResource(imageId)
    it.layoutParams = ViewGroup.LayoutParams(width, height)
    it.scaleX = 0f
    it.scaleY = 0f

    // add constraint layout
    addView(it)
    ViewCompat.setTranslationZ(it, elevation)

    if (direction == Direction.LEFT) {
      (it.layoutParams as ConstraintLayout.LayoutParams).run {
        bottomToTop = R.id.baseView
        endToStart = R.id.baseView
      }
      it.pivotX = width.toFloat()
      it.pivotY = height.toFloat()
    } else {
      (it.layoutParams as ConstraintLayout.LayoutParams).run {
        bottomToTop = R.id.baseView
        startToEnd = R.id.baseView
      }
      it.pivotX = 0f
      it.pivotY = height.toFloat()
    }
  }

  /**
   * read mark down
   */
  private fun readMarkdown(@RawRes rawId: Int) =
    resources.openRawResource(rawId).bufferedReader().use { it.readText() }
}
