package com.theshoremedia.utils.extensions

import android.graphics.Bitmap
import android.view.View
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
    makeVisible(isVisible)
    //TODO: Fix animation related issue

//    if (isVisible) {
//        this.showWithAnimation()
//    } else this.hideWithAnimation()
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
    val bitmap = Bitmap.createBitmap(this.drawingCache)
    this.isDrawingCacheEnabled = false
    return bitmap

}
