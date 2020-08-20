package com.theshoremedia.utils.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.theshoremedia.R
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.DialogUtility

/**
 * @author- Nitin Khanna
 * @date -
 */

class OnDrawPermissionsUtils {
    companion object {
        private lateinit var listener: (() -> Unit)
        private var dialogVisible: Boolean = false

        fun checkPermission(
            mContext: Context,
            listener: (() -> Unit)
        ) {
            Companion.listener = listener
            if (dialogVisible) return
            if (Settings.canDrawOverlays(mContext)) {
                invoke()
                return
            }
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            dialogVisible = true
            DialogUtility.showDialog(
                mContext,
                mContext.getString(R.string.permission_title_draw_overlays),
                message = mContext.getString(R.string.permission_message_draw_overlays),
                hideCancelButton = true,
                responseListener = { action ->
                    if (action == R.string.ok) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + mContext.packageName)
                        )
                        (mContext as Activity).startActivityForResult(
                            intent,
                            AppConstants.PermissionsCode.ACTION_MANAGE_OVERLAY
                        )
                    }
                    dialogVisible =
                        false
                })


        }

        fun invoke() {
            dialogVisible = false
            listener.invoke()
        }

        fun verifyPermission(
            mContext: Context,
            listener: ((isEnabled: Boolean) -> Unit)
        ) {
            listener.invoke(Settings.canDrawOverlays(mContext))
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