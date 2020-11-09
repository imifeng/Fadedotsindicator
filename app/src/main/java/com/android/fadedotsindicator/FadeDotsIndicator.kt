package com.android.fadedotsindicator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class FadeDotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val dots = ArrayList<ImageView>()
    private var dotsClickable: Boolean = false
    private var currentDot: Int = -1

    private val dotsSize: Float
    private val dotsSpacing: Float
    private val dotsCornerRadius: Float

    private val linearLayout = LinearLayout(context)

    init {
        linearLayout.orientation = LinearLayout.HORIZONTAL
        addView(
            linearLayout,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dotsSize = 14.toDp()
        dotsSpacing = 36.toDp()
        dotsCornerRadius = dotsSize / 2
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots()
    }

    private fun refreshDotsCount() {
        if (dots.size < pager!!.count) {
            addDots(pager!!.count - dots.size)
        } else if (dots.size > pager!!.count) {
            removeDots(dots.size - pager!!.count)
        }
    }

    private fun addDots(count: Int) {
        for (i in 0 until count) {
            addDot(i)
        }
    }

    private fun removeDots(count: Int) {
        for (i in 0 until count) {
            removeDot(i)
        }
    }

    private fun addDot(index: Int) {
        val dot = LayoutInflater.from(context).inflate(R.layout.view_fade_dots_indicator, this, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dot.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        val strokeView = dot.findViewById<ImageView>(R.id.dot_stroke)
        setUpDotCornerRadiusView(strokeView)

        val imageView = dot.findViewById<ImageView>(R.id.dot)
        setUpDotCornerRadiusView(imageView)
        setUpDotAlpha(index, imageView)

        dot.setOnClickListener {
            if (dotsClickable && index < pager?.count ?: 0) {
                pager!!.setCurrentItem(index, true)
            }
        }

        dots.add(imageView)
        linearLayout.addView(dot)
    }

    private fun setUpDotCornerRadiusView(imageView: ImageView) {
        val params = imageView.layoutParams as RelativeLayout.LayoutParams
        params.height = dotsSize.toInt()
        params.width = params.height
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        params.setMargins((dotsSpacing / 2).toInt(), 0, (dotsSpacing / 2).toInt(), 0)

        val background = imageView.background as GradientDrawable
        background.cornerRadius = dotsSize / 2
    }

    private fun setUpDotAlpha(index: Int, imageView: ImageView) {
        if (pager!!.currentItem == index) {
            currentDot = pager!!.currentItem
            imageView.alpha = 1f
        } else {
            imageView.alpha = 0f
        }
    }

    fun removeDot(index: Int) {
        linearLayout.removeViewAt(childCount - 1)
        dots.removeAt(dots.size - 1)
    }

    protected fun refreshDots() {
        if (pager == null) {
            return
        }
        post {
            // Check if we need to refresh the dots count
            refreshDotsCount()
            refreshDotsColors()
            refreshOnPageChangedListener()
        }
    }

    private fun refreshOnPageChangedListener() {
        if (pager!!.isNotEmpty) {
            pager!!.removeOnPageChangeListener()
            pager!!.addOnPageChangeListener()
        }
    }

    private fun refreshDotsColors() {
        for (i in dots.indices) {
            setUpDotAlpha(i, dots[i])
        }
    }

    var pager: Pager? = null

    interface Pager {
        val isNotEmpty: Boolean
        val currentItem: Int
        val isEmpty: Boolean
        val count: Int
        fun setCurrentItem(item: Int, smoothScroll: Boolean)
        fun removeOnPageChangeListener()
        fun addOnPageChangeListener()
    }

    // PUBLIC METHODS
    fun setViewPager2(viewPager2: ViewPager2) {
        if (viewPager2.adapter == null) {
            throw IllegalStateException(
                "You have to set an adapter to the view pager before " +
                        "initializing the dots indicator !"
            )
        }

        viewPager2.adapter!!.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                refreshDots()
            }
        })

        pager = object : Pager {
            var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null

            override val isNotEmpty: Boolean
                get() = viewPager2.isNotEmpty
            override val currentItem: Int
                get() = viewPager2.currentItem
            override val isEmpty: Boolean
                get() = viewPager2.isEmpty
            override val count: Int
                get() = viewPager2.adapter?.itemCount ?: 0

            override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
                viewPager2.setCurrentItem(item, smoothScroll)
            }

            override fun removeOnPageChangeListener() {
                onPageChangeCallback?.let { viewPager2.unregisterOnPageChangeCallback(it) }
            }

            override fun addOnPageChangeListener() {
                onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        if (position + 1 >= count || position == -1) {
                            return
                        }
                        dots[position].alpha = 1 - positionOffset
                        dots[position + 1].alpha = positionOffset
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                        if (state == ViewPager2.SCROLL_STATE_IDLE && currentItem != currentDot) {
                            refreshDots()
                        }
                    }
                }
                viewPager2.registerOnPageChangeCallback(onPageChangeCallback!!)
            }
        }

        refreshDots()
    }

    private val ViewPager2.isNotEmpty: Boolean get() = adapter!!.itemCount > 0

    private val ViewPager2?.isEmpty: Boolean
        get() = this != null && this.adapter != null && adapter!!.itemCount == 0
}
