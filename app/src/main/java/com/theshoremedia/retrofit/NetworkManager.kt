package com.theshoremedia.retrofit

import android.content.Context
import android.net.ConnectivityManager
import com.theshoremedia.modules.base.BaseActivity

object NetworkManager {
    var isNetworkEnable = true
    /**
     * Method to check the internet connectivity.
     *
     * @param context Context
     * @return boolean
     */
    private fun isConnectionAvailable(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

    fun init(context: BaseActivity) {
        isNetworkEnable = isConnectionAvailable(context)
    }
}