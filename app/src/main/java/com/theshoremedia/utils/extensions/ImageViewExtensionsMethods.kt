package com.theshoremedia.utils.extensions

import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.LoadRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.theshoremedia.R
import java.util.*


/**
 * @author- Nitin Khanna
 * @date -
 */
fun ImageView.loadImage(resource: Int) {
    Glide.with(this.context).load(resource).into(this)
}

fun ImageView.loadImage(image: String?) {
    if (image.isNullOrEmpty()) return

    if (image.toLowerCase(Locale.ROOT).endsWith("svg")) {
        val context = this.context
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry {
                add(SvgDecoder(context))
            }
            .build()
        val request = LoadRequest.Builder(this.context)
            .data(image)
            .target(this)
            .build()
        imageLoader.execute(request)
    } else {

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


}
