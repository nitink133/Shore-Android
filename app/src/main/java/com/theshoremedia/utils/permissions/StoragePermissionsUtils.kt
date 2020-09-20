package com.theshoremedia.utils.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.DialogUtils
import com.theshoremedia.utils.ToastUtils

/**
 * @author- Nitin Khanna
 * @date -
 */

class StoragePermissionsUtils {
    companion object {
        private lateinit var listener: (() -> Unit)
        private var dialogVisible: Boolean = false

        fun checkPermission(
            mContext: Context,
            listener: (() -> Unit)
        ) {
            Companion.listener = listener
            if (dialogVisible) return
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                invoke()
                return
            }
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            dialogVisible = true
            DialogUtils.showDialog(
                mContext,
                mContext.getString(R.string.permission_title_storage),
                message = mContext.getString(R.string.permission_message_storage),
                responseListener = { action ->
                    if (action == R.string.ok) {
                        ActivityCompat.requestPermissions(
                            mContext as MainActivity,
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            AppConstants.PermissionsCode.ACTION_STORAGE
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

        private fun verifyPermission(
            mContext: Context,
            listener: ((isEnabled: Boolean) -> Unit)
        ) {
            listener.invoke(
                ContextCompat.checkSelfPermission(
                    mContext,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        == PackageManager.PERMISSION_GRANTED
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
                    ToastUtils.makeToast(
                        mContext,
                        mContext.getString(R.string.err_permission_is_required)
                    )
            }
        }
    }
}