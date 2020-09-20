package com.theshoremedia.utils.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author- Nitin Khanna
 * @date -
 */

fun View.makeVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.makeVisibleWithAnimation(
    isVisible: Boolean,
    left: Boolean = false,
    right: Boolean = false,
    top: Boolean = false,
    bottom: Boolean = true
) {
//    makeVisible(isVisible)
    //TODO: Fix animation related issue

    if (isVisible) {
        this.showWithAnimation()
    } else this.hideWithAnimation()
}

fun RecyclerView.validateNoDataView(llNoData: View?) {
    val counts = this.adapter?.itemCount ?: 0
    llNoData?.makeVisible(isVisible = counts == 0)
}

fun View.changeBackgroundColor(colorCode: Int) {
    val context = this.context
    this.setBackgroundColor(context.resources.getColor(colorCode))

}


fun View.getScreenShot(): Bitmap? {
    this.isDrawingCacheEnabled = true
    if (this.drawingCache == null) return null
    val bitmap = Bitmap.createBitmap(this.drawingCache)
    this.isDrawingCacheEnabled = false
    return bitmap

}

fun ScrollView.getScrollViewScreenShot(): Bitmap? {
    val bitmap = Bitmap.createBitmap(
        this.getChildAt(0).width,
        this.getChildAt(0).height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    val bgDrawable: Drawable = this.background
    bgDrawable.draw(canvas)
    this.draw(canvas)
    return bitmap

}