package com.theshoremedia.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random


/**
 * @author- Nitin Khanna
 * @date -
 */


fun Bitmap.save(): File? {
    val dirPath: String =
        Environment.getExternalStorageDirectory().absolutePath.toString() + "/Shore"
    val dir = File(dirPath)
    if (!dir.exists()) dir.mkdirs()
    val file = File(dirPath, "Shore Fact Check- ${Random(1000).nextInt()}.jpg")
    try {
        val fOut = FileOutputStream(file)
        compress(Bitmap.CompressFormat.PNG, 85, fOut)
        fOut.flush()
        fOut.close()

        return file
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
