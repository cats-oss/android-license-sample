package com.github.aakira.license.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class ObservableScrollView : ScrollView {

  var scrollListener: ScrollListener? = null

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  override fun onScrollChanged(x: Int, y: Int, oldl: Int, oldt: Int) {
    super.onScrollChanged(x, y, oldl, oldt)

    scrollListener?.onScrollChanged(this, Math.max(0, x), Math.max(0, y), Math.max(0, oldl), Math.max(0, oldt))
  }

  interface ScrollListener {
    fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
  }
}