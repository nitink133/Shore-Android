package com.theshoremedia.utils

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager

/**
 * @author- Nitin Khanna
 * @date -
 */
object FloatingViewsLayoutParamsUtils {

    fun getWindowType(): Int {
        var windowType = WindowManager.LayoutParams.TYPE_PHONE
        // Set to TYPE_SYSTEM_ALERT so that the Service can display it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            windowType = WindowManager.LayoutParams.TYPE_TOAST
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        return windowType
    }

    fun getDefaultParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
    }

    fun getParamsForAccessibilityPerHelpView(): WindowManager.LayoutParams {
        val params = getDefaultParams()
        params.gravity = Gravity.TOP or Gravity.END
        params.verticalMargin = .2F
        return params
    }
}