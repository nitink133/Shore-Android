package com.theshoremedia.views

import android.content.Context
import android.content.Intent
import com.theshoremedia.floatingview.credibility.services.CredibilityCheckerService
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils

/**
 * @author - Nitin Khanna
 * @date -
 */


class BubbleCredibilityCheckerView(
    private val mContext: Context
) {

    var isShow = false

    fun create() {
        OnDrawPermissionsUtils.verifyPermission(mContext) { isEnabled ->
            if (!isEnabled) return@verifyPermission
            val service = Intent(mContext, CredibilityCheckerService::class.java)
            mContext.startService(service)

        }
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
        if (isShow) return
        instance?.create()

    }

}
