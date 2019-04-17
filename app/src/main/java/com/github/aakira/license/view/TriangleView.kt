package com.github.aakira.license.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import com.github.aakira.license.R
import kotlin.properties.Delegates

class TriangleView : View {

  companion object {
    private const val DEFAULT_COLOR = -0x8a8a8b
  }

  private var paint: Paint by Delegates.notNull()
  private var trianglePath: Path? = null

  constructor(context: Context) : this(context, null)

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {

    val a = context.obtainStyledAttributes(attrs, R.styleable.TriangleView)
    val triangleColor = a.getColor(R.styleable.TriangleView_tv_color, DEFAULT_COLOR)
    a.recycle()

    paint = Paint().apply {
      style = Paint.Style.FILL
      color = triangleColor
      isAntiAlias = true
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawPath(trianglePath(), paint)
  }

  private fun trianglePath(): Path {
    if (trianglePath != null) return trianglePath!!

    val width = width
    val height = height
    val p1: Point
    val p2: Point
    val p3: Point
    p1 = Point(0, height)
    p2 = Point(width, height)
    p3 = Point(width / 2, height / 2)

    trianglePath = Path().apply {
      moveTo(p1.x.toFloat(), p1.y.toFloat())
      lineTo(p2.x.toFloat(), p2.y.toFloat())
      lineTo(p3.x.toFloat(), p3.y.toFloat())
      close()
    }
    return trianglePath!!
  }
}
