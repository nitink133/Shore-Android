package com.theshoremedia.views

import android.content.Context
import android.view.View
import com.theshoremedia.R
import com.theshoremedia.floatingview.general.CallBack
import com.theshoremedia.floatingview.general.FloatingLayout
import com.theshoremedia.utils.AppConstants

/**
 * @author- Nitin Khanna
 * @date -
 */

class BubbleAccessibilityPermissionsHelpView {
    private var mContext: Context? = null

    companion object {
        private var instance: BubbleAccessibilityPermissionsHelpView? = null
        fun getInstance(mContext: Context): BubbleAccessibilityPermissionsHelpView {
            if (instance == null) {
                instance = BubbleAccessibilityPermissionsHelpView().apply {
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
                object :
                    CallBack {
                    override fun onClickListener(resourceId: Int) {
                        floatingLayout?.close()
                    }

                    override fun onCreateListener(view: View?) {}
                    override fun onCloseListener() {}
                })
        floatingLayout?.create()
    }
}