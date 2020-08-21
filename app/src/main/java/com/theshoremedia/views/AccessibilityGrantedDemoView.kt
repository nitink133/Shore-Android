package com.theshoremedia.views

import android.content.Context
import android.view.View
import com.theshoremedia.R
import com.theshoremedia.floatingview.CallBack
import com.theshoremedia.floatingview.FloatingLayout
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.ApplicationUtils
import com.theshoremedia.utils.ToastUtils

/**
 * @author- Nitin Khanna
 * @date -
 */

class AccessibilityGrantedDemoView {
    private var mContext: Context? = null

    companion object {
        private var instance: AccessibilityGrantedDemoView? = null
        fun getInstance(mContext: Context): AccessibilityGrantedDemoView {
            if (instance == null) {
                instance = AccessibilityGrantedDemoView().apply {
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
                R.layout.bubble_accessibility_success,
                mFloatingViewType = AppConstants.FloatingViewType.ACCESSIBILITY_SUCCESS_DEMO,
                callBack =
                object : CallBack {
                    override fun onClickListener(resourceId: Int) {
                        floatingLayout?.close()
                        when (resourceId) {
                            R.id.btnStart ->
                                ToastUtils.makeToast(
                                    mContext,
                                    mContext?.getString(R.string.err_work_is_under_process)
                                )
                            R.id.ivTakeToApp ->
                                ApplicationUtils.startApp(mContext)

                        }
                    }

                    override fun onCreateListener(view: View?) {}
                    override fun onCloseListener() {}
                })
        floatingLayout?.create()
    }
}
