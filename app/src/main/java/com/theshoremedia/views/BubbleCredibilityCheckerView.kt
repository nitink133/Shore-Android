package com.theshoremedia.views

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils


/**
 * @author - Nitin Khanna
 * @date -
 */


class BubbleCredibilityCheckerView(
    private val mContext: Context
) {
    private var isShow: Boolean = false

    fun create() {
        OnDrawPermissionsUtils.verifyPermission(mContext) { isEnabled ->
            if (!isEnabled) return@verifyPermission
            isShow = true
            val serviceIntent = Intent(mContext, CredibilityCheckerService::class.java)
            ContextCompat.startForegroundService(mContext, serviceIntent)
        }
    }

    fun close() {
        Log.d("Nitin", "close() of BubbleCredibilityCheckerView.kt")
        isShow = false
    }


    companion object {
        private var instance: BubbleCredibilityCheckerView? = null
        fun getInstance(mContext: Context): BubbleCredibilityCheckerView {
            if (instance == null) {
                instance = BubbleCredibilityCheckerView(mContext)
            }
            return instance!!
        }
    }


    fun init() {
        if (CredibilityCheckerService.isInitialized()) return
        if (isShow) return
        Log.d("Nitin", "initService")
        instance?.create()
    }

}
