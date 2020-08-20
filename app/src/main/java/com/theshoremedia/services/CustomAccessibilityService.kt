package com.theshoremedia.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.theshoremedia.R
import com.theshoremedia.utils.extensions.debugView

/**
 * Created by sotsys-014 on 4/10/16.
 */
class CustomAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        Log.d("Nitin", "onServiceConnected")
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.packageNames = arrayOf(getString(R.string.whatsapp_package_name))
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            val currentNode: AccessibilityNodeInfo = rootInActiveWindow
            debugView(currentNode)
        }
    }

    override fun onInterrupt() {
        Log.d("Nitin", "onInterrupt")
    }
}