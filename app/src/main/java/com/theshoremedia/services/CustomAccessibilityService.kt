package com.theshoremedia.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.preference.PreferenceManager
import com.theshoremedia.R
import com.theshoremedia.utils.PreferenceUtils
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.WhatsAppUtils
import com.theshoremedia.views.BubbleCredibilityCheckerView


/**
 * Created by sotsys-014 on 4/10/16.
 */
class CustomAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        Log.d("Nitin", "onServiceConnected")
//        ToastUtils.makeToast(this, "Service connected")
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.packageNames = arrayOf(getString(R.string.whatsapp_package_name))
        serviceInfo = info
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        val isAutoDetectEnable = PreferenceUtils.getPref<Boolean>(getString(R.string.key_auto_detect))?:return
//        if (isAutoDetectEnable) {
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                val currentNode: AccessibilityNodeInfo = rootInActiveWindow
                WhatsAppUtils.debugView(this, currentNode)
            }
//        }
    }

    override fun onInterrupt() {
        Log.d("Nitin", "onInterrupt")
//        ToastUtils.makeToast(this, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
//        ToastUtils.makeToast(this, "Service destroyed")
    }

    override fun onCreate() {
        super.onCreate()
        ToastUtils.makeToast(this, "Service created")
    }
}