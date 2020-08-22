package com.theshoremedia.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.theshoremedia.BuildConfig


/**
 * @author- Nitin Khanna
 * @date -
 */
object ApplicationUtils {
    fun startApp(mContext: Context?) {
        if (mContext == null) return
        val packageName = BuildConfig.APPLICATION_ID
        var intent: Intent? = mContext.packageManager.getLaunchIntentForPackage(packageName)

        if (intent == null) {
            intent = try {
                // if play store installed, open play store, else open browser
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            } catch (e: Exception) {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            }
        }
        mContext.startActivity(intent)
    }
}
