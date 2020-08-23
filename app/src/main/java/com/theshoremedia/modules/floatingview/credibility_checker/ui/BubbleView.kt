package com.theshoremedia.modules.floatingview.credibility_checker.ui

import android.graphics.Paint
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringListener
import com.facebook.rebound.SpringSystem
import com.theshoremedia.R
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.FloatingViewsLayoutParamsUtils
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.getScreenSize
import kotlin.math.pow


class BubbleView(var rootView: RootView) :
    FrameLayout(rootView.context), View.OnTouchListener,
    SpringListener {
    var params = FloatingViewsLayoutParamsUtils.getDefaultParams(
        flag = 0
    )

    var springSystem: SpringSystem = SpringSystem.create()

    var springX: Spring = springSystem.createSpring()
    var springY: Spring = springSystem.createSpring()

    private val paint = Paint()

    private var initialX = 0.0f
    private var initialY = 0.0f

    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f

    private var moving = false

    private var tvUnreadCount: TextView
    var llUnreadCount: LinearLayout

    var notifications = 0
        set(value) {
            if (value >= 0) field = value

            if (value == 0) {
                llUnreadCount.visibility = GONE
            } else if (value > 0) {
                llUnreadCount.visibility = VISIBLE
                tvUnreadCount.text = "$value"
            }
        }

    override fun onSpringEndStateChange(spring: Spring?) = Unit
    override fun onSpringAtRest(spring: Spring?) = Unit
    override fun onSpringActivate(spring: Spring?) = Unit

    init {
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0
        params.width = AppConstants.OverlayViewSize.CHAT_HEAD_SIZE + 15
        params.height = AppConstants.OverlayViewSize.CHAT_HEAD_SIZE + 30

        val view = inflate(context, R.layout.bubble_shore_icon, this)

        val ivShoreLogo: ImageView = view.findViewById(R.id.ivShoreLogo)
        tvUnreadCount = view.findViewById(R.id.tvUnreadCount)
        llUnreadCount = view.findViewById(R.id.llUnreadCount)

        springX.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                x = spring.currentValue.toFloat()
            }
        })

        springX.springConfig =
            SpringConfigs.NOT_DRAGGING
        springX.addListener(this)

        springY.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                y = spring.currentValue.toFloat()
            }
        })
        springY.springConfig =
            SpringConfigs.NOT_DRAGGING
        springY.addListener(this)

        this.setLayerType(View.LAYER_TYPE_HARDWARE, paint)

        rootView.addView(this, params)

        this.setOnTouchListener(this)

    }


    override fun onSpringUpdate(spring: Spring) {
        if (spring !== this.springX && spring !== this.springY) return
        val totalVelocity = Math.hypot(springX.velocity, springY.velocity).toInt()

        rootView.onContentSpringUpdate(this, spring, totalVelocity)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val currentChatHead = rootView

        val metrics = getScreenSize()

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = x
                initialY = y
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                scaleX = 0.92f
                scaleY = 0.92f
            }
            MotionEvent.ACTION_UP -> {
                if (!moving) {
                    rootView.collapse()
                } else {
                    springX.endValue =
                        metrics.widthPixels - width - 0 * (width + AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_PADDING).toDouble()
                    springY.endValue =
                        AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_MARGIN_TOP.toDouble()

                    rootView.showContentView()
                }

                scaleX = 1f
                scaleY = 1f

                moving = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (AppConstants.OverlayViewSize.distance(
                        initialTouchX,
                        event.rawX,
                        initialTouchY,
                        event.rawY
                    ) > AppConstants.OverlayViewSize.CHAT_HEAD_DRAG_TOLERANCE.pow(2) && !moving
                ) {
                    moving = true

                    rootView.hideContentView()
                }

                if (moving) {
                    springX.currentValue = initialX + (event.rawX - initialTouchX).toDouble()
                    springY.currentValue = initialY + (event.rawY - initialTouchY).toDouble()
                }
            }
        }

        return true
    }


}