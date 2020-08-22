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

    private fun getWindowType(): Int {
        var windowType = WindowManager.LayoutParams.TYPE_TOAST
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        return windowType
    }

    fun getDefaultParams(
        width: Int = WindowManager.LayoutParams.WRAP_CONTENT,
        height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
        gravity: Int = Gravity.CENTER,
        flag: Int = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        verticalMargin: Float = 0F,
        horizontalMargin: Float = 0F,
        dimAmount: Float? = null
    ): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams(
            width,
            height,
            getWindowType(),
            flag,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = gravity
        params.verticalMargin = verticalMargin
        params.horizontalMargin = horizontalMargin
        if (dimAmount != null) params.dimAmount = dimAmount
        return params
    }


}