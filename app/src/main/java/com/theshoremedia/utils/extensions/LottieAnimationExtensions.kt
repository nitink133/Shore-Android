package com.theshoremedia.utils.extensions

import android.animation.ValueAnimator
import com.airbnb.lottie.LottieAnimationView


/**
 * @author- Nitin Khanna
 * @date -
 */
fun LottieAnimationView.startCheckAnimation() {
    val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(500)
    animator.addUpdateListener { valueAnimator: ValueAnimator ->
        progress = (valueAnimator.animatedValue as Float)
    }
    if (progress == 0f) {
        animator.start()
    } else {
        progress = 0f
    }
}

fun LottieAnimationView.setFullAnimation() {
    val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(500)
    animator.addUpdateListener { valueAnimator: ValueAnimator ->
        progress = (valueAnimator.animatedValue as Float)
    }
    if (progress == 0f) {
        animator.start()
    }
}

fun LottieAnimationView.setZeroAnimation() {
    val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(500)
    animator.addUpdateListener { valueAnimator: ValueAnimator ->
        progress = (valueAnimator.animatedValue as Float)
    }
    progress = 0f
}

fun LottieAnimationView.setZero() {
    progress = 0f
}

fun LottieAnimationView.setFull() {
    progress = 1f
}