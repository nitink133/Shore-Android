package com.theshoremedia.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.theshoremedia.R


/**
 * @author- Nitin Khanna
 * @date -
 */
fun ImageView.loadImage(resource: Int) {
    Glide.with(this.context).load(resource).into(this)
}

fun ImageView.loadImage(image: String?) {
    if (image.isNullOrEmpty()) return

    val options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.anim_progress_bar)
        .error(R.drawable.ic_logo)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)
        .dontAnimate()
        .dontTransform()
    Glide.with(this.context).load(image).apply(options).into(this)


}
