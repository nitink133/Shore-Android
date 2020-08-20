package com.theshoremedia.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AnimationUtils
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

fun View.showWithAnimation() {
    if(this.isVisible)return
    this.makeVisible(true)
    this.alpha = 0.0f;
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(1.0f)
        .setListener(null);
}


fun View.hideWithAnimation() {
    if(!this.isVisible)return
    val view = this
    view.animate()
        .translationY(0F)
        .alpha(0.0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.makeVisible(isVisible = false)
            }
        })

}