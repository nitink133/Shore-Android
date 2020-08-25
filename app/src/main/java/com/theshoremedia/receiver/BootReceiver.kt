package com.theshoremedia.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService.enqueueWork
import com.theshoremedia.services.CustomAccessibilityService


/**
 * @author- Nitin Khanna
 * @date -
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
//            CustomAccessibilityService.enqueueWork1(context, Intent())
        }
    }
}