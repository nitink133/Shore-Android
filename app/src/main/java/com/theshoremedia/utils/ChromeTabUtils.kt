package com.theshoremedia.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent


/**
 * @author- Nitin Khanna
 * @date -
 */

object ChromeTabUtils {
    fun openLinkInChromeTab(link: String?, mContext: Context?) {
        if (link.isNullOrEmpty() || mContext == null) return
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }
}