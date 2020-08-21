package com.theshoremedia.utils.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import com.theshoremedia.R
import com.theshoremedia.services.CustomAccessibilityService
import com.theshoremedia.utils.AccessibilityUtils
import com.theshoremedia.utils.DialogUtility
import com.theshoremedia.views.AccessibilityGrantedDemoView
import com.theshoremedia.views.AccessibilityPermissionsHelpView

/**
 * @author- Nitin Khanna
 * @date -
 */

class AccessibilityPermissionsUtils {
    companion object {
        private lateinit var listener: (() -> Unit)
        private var dialogVisible: Boolean = false

        fun checkPermission(
            mContext: Context,
            listener: (() -> Unit)
        ) {
            Companion.listener = listener
            if (dialogVisible) return

            if (AccessibilityUtils.isAccessibilityServiceEnabled(
                    mContext,
                    CustomAccessibilityService::class.java
                )
            ) {
                invoke()
                return
            }

            registerListener(mContext, listener)

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            dialogVisible = true
            DialogUtility.showDialog(
                mContext,
                mContext.getString(R.string.permission_title_accessibility),
                message = mContext.getString(R.string.permission_message_accessibility),
                responseListener = { action ->
                    if (action == R.string.ok) {

                        AccessibilityPermissionsHelpView.getInstance(mContext).init()
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        (mContext as Activity).startActivity(intent)

                    }
                    dialogVisible =
                        false
                })


        }

        private fun registerListener(mContext: Context, listener: () -> Unit) {
            val observer: ContentObserver = object : ContentObserver(Handler()) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    verifyPermission(mContext) { isEnabled ->
                        if (isEnabled) {
                            mContext.contentResolver.unregisterContentObserver(this);
                            AccessibilityGrantedDemoView.getInstance(mContext).init()
                        }
                    }
                }
            }

            val uri: Uri =
                Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            mContext.contentResolver.registerContentObserver(uri, false, observer)


        }

        fun invoke() {
            dialogVisible = false
            listener.invoke()
        }

        private fun verifyPermission(
            mContext: Context,
            listener: ((isEnabled: Boolean) -> Unit)
        ) {
            listener.invoke(
                AccessibilityUtils.isAccessibilityServiceEnabled(
                    mContext,
                    CustomAccessibilityService::class.java
                )
            )
        }


        fun onActivityResult(
            mContext: Context?
        ) {
            if (mContext == null) return
            verifyPermission(mContext) { isEnabled ->
                if (isEnabled)
                    listener.invoke()
                else
                    checkPermission(
                        mContext,
                        listener
                    )
            }

        }
    }
}