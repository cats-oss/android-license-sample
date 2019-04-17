package com.github.aakira.license.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.github.aakira.license.R

class BicycleView : AppCompatImageView {

  companion object {
    // amount of movement of y to move forward one frame
    private const val THRESHOLD_DY = 100
  }

  constructor(context: Context) : this(context, null)

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  fun position(y: Int) {
    // left -> center -> right -> center -> left...
    setImageResource(
      when (y / THRESHOLD_DY % 4) {
        0 -> R.drawable.ic_bike_center
        1 -> R.drawable.ic_bike_left
        2 -> R.drawable.ic_bike_center
        else -> R.drawable.ic_bike_right
      }
    )
  }
}
