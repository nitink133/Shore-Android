package com.theshoremedia.modules.floatingview.credibility_checker.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.theshoremedia.R
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.FloatingViewsLayoutParamsUtils
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.dpToPx
import com.theshoremedia.utils.extensions.getScreenSize

class CloseView(var chatHeads: RootView) :
    View(chatHeads.context) {
    private var params = FloatingViewsLayoutParamsUtils.getDefaultParams(
        width = AppConstants.OverlayViewSize.CLOSE_SIZE + AppConstants.OverlayViewSize.CLOSE_ADDITIONAL_SIZE,
        height = AppConstants.OverlayViewSize.CLOSE_SIZE + AppConstants.OverlayViewSize.CLOSE_ADDITIONAL_SIZE,
        flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )

    private var gradientParams =
        FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dpToPx(150f))

    var springSystem = SpringSystem.create()

    var springY = springSystem.createSpring()
    var springX = springSystem.createSpring()
    var springAlpha = springSystem.createSpring()
    var springScale = springSystem.createSpring()

    val paint = Paint()

    val gradient = FrameLayout(context)

    var hidden = true

    private var bitmapBg = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(
            CredibilityCheckerService.getInstance().resources, R.drawable.close_bg
        ),
        AppConstants.OverlayViewSize.CLOSE_SIZE,
        AppConstants.OverlayViewSize.CLOSE_SIZE, false
    )!!
    private val bitmapClose = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(
            CredibilityCheckerService.getInstance().resources, R.drawable.close
        ), dpToPx(28f), dpToPx(28f), false
    )!!

    fun hide() {
        val metrics = getScreenSize()
        springY.endValue = metrics.heightPixels.toDouble() + height
        springX.endValue = metrics.widthPixels.toDouble() / 2 - width / 2

        springAlpha.endValue = 0.0
        hidden = true
    }

    fun show() {
        hidden = false
        visibility = View.VISIBLE

        springAlpha.endValue = 1.0
        resetScale()
    }

    fun enlarge() {
        springScale.endValue = AppConstants.OverlayViewSize.CLOSE_ADDITIONAL_SIZE.toDouble()
    }

    fun resetScale() {
        springScale.endValue = 1.0
    }

    fun onPositionUpdate() {
        if (chatHeads.closeCaptured) {
            chatHeads.bubbleView!!.springX.endValue =
                springX.endValue + width / 2 - chatHeads.bubbleView!!.params.width / 2 + 2
            chatHeads.bubbleView!!.springY.endValue =
                springY.endValue + height / 2 - chatHeads.bubbleView!!.params.height / 2 + 2
        }
    }

    init {
        this.setLayerType(View.LAYER_TYPE_HARDWARE, paint)

        visibility = View.INVISIBLE
        hide()

        springY.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                y = spring.currentValue.toFloat()

                if (chatHeads.closeCaptured && chatHeads.wasMoving) {
                    chatHeads.bubbleView!!.springY.currentValue = spring.currentValue
                }

                onPositionUpdate()
            }
        })

        springX.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                x = spring.currentValue.toFloat()

                onPositionUpdate()
            }
        })

        springScale.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                bitmapBg = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(
                        CredibilityCheckerService.getInstance().resources, R.drawable.close_bg
                    ),
                    (spring.currentValue + AppConstants.OverlayViewSize.CLOSE_SIZE).toInt(),
                    (spring.currentValue + AppConstants.OverlayViewSize.CLOSE_SIZE).toInt(),
                    false
                )
                invalidate()
            }
        })

        springAlpha.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                gradient.alpha = spring.currentValue.toFloat()
            }
        })

        springScale.springConfig =
            SpringConfigs.CLOSE_SCALE
        springY.springConfig = SpringConfigs.CLOSE_Y

        params.gravity = Gravity.START or Gravity.TOP
        gradientParams.gravity = Gravity.BOTTOM

        gradient.background = ContextCompat.getDrawable(context, R.drawable.bg_gradient_grey)
        springAlpha.currentValue = 0.0

        z = 100f

        chatHeads.addView(this, params)
        chatHeads.addView(gradient, gradientParams)
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(
            bitmapBg,
            width / 2 - bitmapBg.width.toFloat() / 2,
            height / 2 - bitmapBg.height.toFloat() / 2,
            paint
        )
        canvas?.drawBitmap(
            bitmapClose,
            width / 2 - bitmapClose.width.toFloat() / 2,
            height / 2 - bitmapClose.height.toFloat() / 2,
            paint
        )
    }
}