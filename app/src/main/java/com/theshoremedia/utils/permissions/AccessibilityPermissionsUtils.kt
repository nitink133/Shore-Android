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
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.DialogUtils
import com.theshoremedia.views.BubbleAccessibilityGrantedDemoView
import com.theshoremedia.views.BubbleAccessibilityPermissionsHelpView

/**
 * @author- Nitin Khanna
 * @date -
 */

class AccessibilityPermissionsUtils {
    companion object {
        private var listener: ((isEnabled: Boolean) -> Unit)? = null
        private var dialogVisible: Boolean = false

        fun checkPermission(
            mContext: Context,
            listener: ((isEnabled: Boolean) -> Unit)
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
            DialogUtils.showDialog(
                mContext,
                mContext.getString(R.string.permission_title_accessibility),
                message = mContext.getString(R.string.permission_message_accessibility),
                responseListener = { action ->
                    if (action == R.string.ok) {

                        BubbleAccessibilityPermissionsHelpView.getInstance(mContext).init()
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        (mContext as Activity).startActivityForResult(intent,AppConstants.PermissionsCode.ACTION_ACCESSIBILITY)

                    } else {
                        listener.invoke(false)
                    }
                    dialogVisible =
                        false
                })


        }

        private fun registerListener(mContext: Context, listener: (isEnabled: Boolean) -> Unit) {
            val observer: ContentObserver = object : ContentObserver(Handler()) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    verifyPermission(mContext) { isEnabled ->
                        if (isEnabled) {
                            mContext.contentResolver.unregisterContentObserver(this)
                            BubbleAccessibilityGrantedDemoView.getInstance(mContext).init()
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
            listener?.invoke(true)
        }

         fun verifyPermission(
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
                listener?.invoke(isEnabled)
            }

        }
    }
}