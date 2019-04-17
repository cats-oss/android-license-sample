package com.github.aakira.license.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_road.view.*
import kotlin.properties.Delegates

class RoadView : ConstraintLayout {

  companion object {
    private const val LINE_OFFSET = 2000

    // graph of factor
    // When x reaches the height of the screen, it seeks y is 1
    // https://docs.google.com/spreadsheets/d/19MNZnu5E56emzYd1NAsaBBy4eDTrPvMvSqkmJ9gzR-o/edit?usp=sharing
    private const val SCALE_FACTOR = 3.5
    private const val Y_FACTOR = 7.5
  }

  // line movement amount
  private var dy: Float by Delegates.notNull()
  // margin of lines
  private var marginLine: Float by Delegates.notNull()

  init {
    LayoutInflater.from(context).inflate(com.github.aakira.license.R.layout.view_road, this, true)
  }

  constructor(context: Context) : this(context, null)

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {

    rootView.viewTreeObserver.addOnGlobalLayoutListener(
      object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
          rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)

          line1.pivotX = line1.measuredWidth.toFloat() / 2
          line1.pivotY = 0f
          line2.pivotX = line2.measuredWidth.toFloat() / 2
          line2.pivotY = 0f
          line3.pivotX = line3.measuredWidth.toFloat() / 2
          line3.pivotY = 0f
          line4.pivotX = line4.measuredWidth.toFloat() / 2
          line4.pivotY = 0f

          val positionY = height / 2f - line1.height
          line1.marginY(positionY.toInt())
          line2.marginY(positionY.toInt())
          line3.marginY(positionY.toInt())
          line4.marginY(positionY.toInt())

          dy = height / 2f
          marginLine = (dy - line1.height * 2) / 2  // height = line height*2 + margin*2
          position(0, LINE_OFFSET)
        }
      })
  }

  /**
   * y current position
   * dy total movement (half the height of screen)
   * scaleFactor factor for scale
   * yFactor factor for dy
   *
   * y=(1 / h^n) * x^n
   * https://docs.google.com/spreadsheets/d/18L7ELAOvYFlnyRuj-T_5yDBZDgzLFjkvj8rHRkcF230/edit#gid=0
   */
  private fun View.linePosition(y: Int, dy: Float, scaleFactor: Double, yFactor: Double) {
    // (1 / h^n) * x^n
    (Math.pow(y.toDouble(), scaleFactor) / Math.pow(dy.toDouble(), scaleFactor)).toFloat().let {
      scaleX = it
      scaleY = it
    }
    translationY = y * (Math.pow(y.toDouble(), yFactor) / Math.pow(dy.toDouble(), yFactor)).toFloat()
  }

  fun position(y: Int, offset: Int = LINE_OFFSET) {
    val position = y + offset

    val firstY = Math.max(0f, position % dy)
    line1.linePosition(firstY.toInt(), dy, SCALE_FACTOR, Y_FACTOR)

    val secondY = Math.max(0f, (position + marginLine) % dy)
    line2.linePosition(secondY.toInt(), dy, SCALE_FACTOR, Y_FACTOR)

    val thirdY = Math.max(0f, (position + marginLine * 2) % dy)
    line3.linePosition(thirdY.toInt(), dy, SCALE_FACTOR, Y_FACTOR)

    val forthY = Math.max(0f, (position + marginLine * 3) % dy)
    line4.linePosition(forthY.toInt(), dy, SCALE_FACTOR, Y_FACTOR)
  }

  private fun View.marginY(margin: Int) {
    val marginLayoutParams = layoutParams as ConstraintLayout.LayoutParams

    marginLayoutParams.setMargins(
      marginLayoutParams.leftMargin, marginLayoutParams.topMargin,
      marginLayoutParams.rightMargin, margin
    )
    layoutParams = marginLayoutParams
  }
}
