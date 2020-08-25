package com.theshoremedia.utils.permissions

import android.app.Activity
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.theshoremedia.BuildConfig
import com.theshoremedia.utils.AppConstants


/**
 * @author- Nitin Khanna
 * @date -
 */

class BatteryOptimizationPermissionsUtils {
    companion object {
        private lateinit var listener: ((isEnabled: Boolean) -> Unit)

        fun checkPermission(
            mContext: Context,
            listener: ((isEnabled: Boolean) -> Unit)
        ) {
            Companion.listener = listener

            val pm = mContext.getSystemService(POWER_SERVICE) as PowerManager? ?: return
            if (pm.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
                invoke()
                return
            }


            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            (mContext as Activity).startActivityForResult(
                intent,
                AppConstants.PermissionsCode.ACTION_BATTERY_SAVER
            )


        }


        fun invoke() {
            listener.invoke(true)
        }

        private fun verifyPermission(
            mContext: Context,
            listener: ((isEnabled: Boolean) -> Unit)
        ) {

            val pm = mContext.getSystemService(POWER_SERVICE) as PowerManager? ?: return
            listener.invoke(pm.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID))
        }


        fun onActivityResult(
            mContext: Context?
        ) {
            if (mContext == null) return
            verifyPermission(mContext) { isEnabled ->
                listener.invoke(isEnabled)
            }

        }
    }
}