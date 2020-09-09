package com.theshoremedia.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * @author- Nitin Khanna
 * @date -
 */
fun ImageView.loadImage(resource: Int) {
    Glide.with(this.context).load(resource).into(this)
}

fun ImageView.loadImage(image: String?) {
    if (image.isNullOrEmpty()) return
    Glide.with(this.context).load(image).into(this)
}
