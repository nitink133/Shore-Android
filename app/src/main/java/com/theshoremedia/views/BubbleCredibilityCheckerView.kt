package com.theshoremedia.views

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils
import com.theshoremedia.utils.whatsapp.WhatsAppUtils


/**
 * @author - Nitin Khanna
 * @date -
 */


class BubbleCredibilityCheckerView(
    private val mContext: Context
) {
    private var isShow: Boolean = false

    fun create() {
        if (WhatsAppUtils.mContext == null) return
        OnDrawPermissionsUtils.verifyPermission(WhatsAppUtils.mContext!!) { isEnabled ->
            if (!isEnabled) return@verifyPermission
            isShow = true
            Thread {
                val serviceIntent =
                    Intent(mContext, CredibilityCheckerService::class.java)
                ContextCompat.startForegroundService(WhatsAppUtils.mContext!!, serviceIntent)
            }.start()
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

        fun getInstance(): BubbleCredibilityCheckerView? = instance
    }


    fun init(isNewData: Boolean = false) {

        if (CredibilityCheckerService.isInitialized) {
            if (isNewData)
                CredibilityCheckerService.getInstance().newDataListener.invoke()
            return
        }
        if (isShow) return
        Log.d("Nitin", "initService")
        instance?.create()
    }

}
