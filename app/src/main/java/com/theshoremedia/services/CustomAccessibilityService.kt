package com.theshoremedia.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.theshoremedia.R
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.AccessibilityUtils
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.PreferenceUtils
import com.theshoremedia.utils.WhatsAppUtils


/**
 * Created by sotsys-014 on 4/10/16.
 */
class CustomAccessibilityService : AccessibilityService() {
    private var lastTriggeredTime: Long = 0L

    companion object {
        private lateinit var instance: CustomAccessibilityService
        private var initialized = false

        fun getInstance(): CustomAccessibilityService {
            return instance
        }

        fun isInitialized(): Boolean =
            initialized
    }

    override fun onServiceConnected() {
        Log.v("Nitin", "onServiceConnected")
        instance = this
        initialized = true

        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.packageNames = arrayOf(getString(R.string.whatsapp_package_name))
        serviceInfo = info
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        try {
            if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return
            if (event.className.toString() != "android.widget.ListView") return
            if (PreferenceUtils.getPref<Boolean>(getString(R.string.key_auto_detect)) == false) return

            if (!AccessibilityUtils.isContinuousScrolling(
                    lastTriggeredTime = lastTriggeredTime,
                    currentTriggeredTime = event.eventTime
                )
            ) {
                lastTriggeredTime = event.eventTime
                return
            }

            lastTriggeredTime = event.eventTime

            Log.d("Nitin", "eventCall Enter")

            val currentNode: AccessibilityNodeInfo = rootInActiveWindow ?: return
            WhatsAppUtils.getInstance(this).debugView(rootNodeInfo = currentNode)
        } catch (e: Exception) {
            CredibilityCheckerService.getInstance().removeBubbleView()
        }
    }

    override fun onInterrupt() {
        initialized = false
        if (CredibilityCheckerService.isInitialized())
            CredibilityCheckerService.getInstance().removeBubbleView()
        Log.d("Nitin", "AccessibilityService- onDestroy()")
    }

    override fun onDestroy() {
        super.onDestroy()
        initialized = false
        Log.d("Nitin", "AccessibilityService- onDestroy()")
        if (CredibilityCheckerService.isInitialized())
            CredibilityCheckerService.getInstance().removeBubbleView()
    }

    override fun onCreate() {
        super.onCreate()
        Log.v("Nitin", "onServiceCreate")
    }
}