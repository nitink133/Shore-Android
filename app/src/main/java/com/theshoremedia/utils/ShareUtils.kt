package com.theshoremedia.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.theshoremedia.BuildConfig
import com.theshoremedia.utils.extensions.getScreenShot
import com.theshoremedia.utils.extensions.save

/**
 * @author- Nitin Khanna
 * @date - 08-09-2020
 */
object ShareUtils {
    fun takeScreenshotAndShare(view: View) {
        val bitmap = view.getScreenShot() ?: return
        val file = bitmap.save() ?: return
        val uri: Uri =
            FileProvider.getUriForFile(view.context, "${BuildConfig.APPLICATION_ID}.provider", file)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            val chooserIntent = Intent.createChooser(intent, "Share Facts")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(view.context, chooserIntent, null)
        } catch (e: ActivityNotFoundException) {
            ToastUtils.makeToast(view.context, "No App Available", Toast.LENGTH_SHORT)
        }
    }
}