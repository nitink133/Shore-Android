package com.theshoremedia.utils.extensions

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.core.view.isVisible
import com.theshoremedia.R


/**
 * @author- Nitin Khanna
 * @date -
 */
fun View.animateFromBottomCentered() {
    this.animation = AnimationUtils.loadAnimation(this.context, R.anim.enter_from_bottom_centered)
}

fun View.animateFromBottom() {
    this.animation = AnimationUtils.loadAnimation(this.context, R.anim.enter_from_bottom)
}

fun View.animateEnterFromLeft() {
    this.animation = AnimationUtils.loadAnimation(this.context, R.anim.enter_from_left)
}

fun View.animateEnterFromRight() {
    this.animation = AnimationUtils.loadAnimation(this.context, R.anim.enter_from_right)
}

fun View.animateTopToBottom() {
    this.animation = AnimationUtils.loadAnimation(this.context, R.anim.enter_from_top)
}

fun View.showWithAnimation() {
    if (this.isVisible) return
    this.slideAnimation(direction = SlideDirection.UP, type = SlideType.SHOW, duration = 600)
}


fun View.hideWithAnimation() {
    if (!this.isVisible) return

    this.slideAnimation(direction = SlideDirection.DOWN, type = SlideType.HIDE, duration = 600)

}

fun TextView.verticleText(text: String?) {
    if (text.isNullOrEmpty()) return
    var newText = ""
    repeat(text.toString().length) {
        if (text[it].toString() != " ")
            newText += text[it] + "\n"
    }
    this.text = newText
}

enum class SlideDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

enum class SlideType {
    SHOW,
    HIDE
}

fun View.slideAnimation(direction: SlideDirection, type: SlideType, duration: Long = 250) {
    val fromX: Float
    val toX: Float
    val fromY: Float
    val toY: Float
    val array = IntArray(2)
    getLocationInWindow(array)
    if ((type == SlideType.HIDE && (direction == SlideDirection.RIGHT || direction == SlideDirection.DOWN)) ||
        (type == SlideType.SHOW && (direction == SlideDirection.LEFT || direction == SlideDirection.UP))
    ) {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels
        array[0] = deviceWidth
        array[1] = deviceHeight
    }
    when (direction) {
        SlideDirection.UP -> {
            fromX = 0f
            toX = 0f
            fromY = if (type == SlideType.HIDE) 0f else (array[1] + height).toFloat()
            toY = if (type == SlideType.HIDE) -1f * (array[1] + height) else 0f
        }
        SlideDirection.DOWN -> {
            fromX = 0f
            toX = 0f
            fromY = if (type == SlideType.HIDE) 0f else -1f * (array[1] + height)
            toY = if (type == SlideType.HIDE) 1f * (array[1] + height) else 0f
        }
        SlideDirection.LEFT -> {
            fromX = if (type == SlideType.HIDE) 0f else 1f * (array[0] + width)
            toX = if (type == SlideType.HIDE) -1f * (array[0] + width) else 0f
            fromY = 0f
            toY = 0f
        }
        SlideDirection.RIGHT -> {
            fromX = if (type == SlideType.HIDE) 0f else -1f * (array[0] + width)
            toX = if (type == SlideType.HIDE) 1f * (array[0] + width) else 0f
            fromY = 0f
            toY = 0f
        }
    }
    val animate = TranslateAnimation(
        fromX,
        toX,
        fromY,
        toY
    )
    animate.duration = duration
    animate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            if (type == SlideType.HIDE) {
                visibility = View.INVISIBLE
            }
        }

        override fun onAnimationStart(animation: Animation?) {
            visibility = View.VISIBLE
        }

    })
    startAnimation(animate)
}