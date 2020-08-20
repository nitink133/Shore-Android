package com.theshoremedia.modules.base

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * @author- Nitin Khanna
 * @date -
 */
open class BaseFragment :Fragment(){
    var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }
}