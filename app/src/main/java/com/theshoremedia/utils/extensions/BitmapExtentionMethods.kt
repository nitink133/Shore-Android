package com.theshoremedia.utils.extensions

import android.graphics.Bitmap
import android.os.Environment
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
