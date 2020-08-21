package com.theshoremedia.utils

import android.content.Context
import android.widget.Toast

/**
 * @author- Nitin Khanna
 * @date -
 */
object ToastUtils {

    fun makeToast(mContext: Context?, value: String?, length: Int = Toast.LENGTH_SHORT) {
        if (mContext== null || value.isNullOrEmpty()) return
        Toast.makeText(mContext, value, length).show()
    }

}