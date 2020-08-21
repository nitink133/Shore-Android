package com.theshoremedia.views

import android.content.Context
import android.view.Gravity
import android.view.View
import com.theshoremedia.R
import com.theshoremedia.floatingview.CallBack
import com.theshoremedia.floatingview.FloatingLayout
import com.theshoremedia.utils.AppConstants

/**
 * @author- Nitin Khanna
 * @date -
 */

class AccessibilityPermissionsHelpView {
    private var mContext: Context? = null

    companion object {
        private var instance: AccessibilityPermissionsHelpView? = null
        fun getInstance(mContext: Context): AccessibilityPermissionsHelpView {
            if (instance == null) {
                instance = AccessibilityPermissionsHelpView().apply {
                    this.mContext = mContext
                }
            }
            return instance!!
        }
    }

    var floatingLayout: FloatingLayout? = null
    fun init() {
        floatingLayout =
            FloatingLayout(
                mContext!!,
                R.layout.bubble_accessibility_help,
                mFloatingViewType = AppConstants.FloatingViewType.ACCESSIBILITY_HELP,
                mLayoutMovable = false,
                callBack =
                object : CallBack {
                    override fun onClickListener(resourceId: Int) {
                        floatingLayout?.close()
                    }

                    override fun onCreateListener(view: View?) {}
                    override fun onCloseListener() {}
                })
        floatingLayout?.create()
    }
}