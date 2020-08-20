package com.theshoremedia.utils

import android.content.Context
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log

object AccessibilityUtils {
    private val TAG = AccessibilityUtils::class.java.simpleName

    /**
     * Check if Accessibility Service is enabled.
     *
     * @param mContext
     * @return `true` if Accessibility Service is ON, otherwise `false`
     */
    fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        //your package /   accesibility service path/class
        val service =
            "com.example.sotsys_014.accessibilityexample/com.accessibilityexample.Service.MyAccessibilityService"
        val accessibilityFound = false
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(
                TAG,
                "accessibilityEnabled = $accessibilityEnabled"
            )
        } catch (e: SettingNotFoundException) {
            Log.e(
                TAG,
                "Error finding setting, default accessibility to not found: "
                        + e.message
            )
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(
                TAG,
                "***ACCESSIBILIY IS ENABLED*** -----------------"
            )
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessabilityService = mStringColonSplitter.next()
                    Log.v(
                        TAG,
                        "-------------- > accessabilityService :: $accessabilityService"
                    )
                    if (accessabilityService.equals(service, ignoreCase = true)) {
                        Log.v(
                            TAG,
                            "We've found the correct setting - accessibility is switched on!"
                        )
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***")
        }
        return accessibilityFound
    }
}