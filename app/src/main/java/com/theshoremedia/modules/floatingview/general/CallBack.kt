package com.theshoremedia.modules.floatingview.general

import android.view.View

interface CallBack {

    fun onClickListener(resourceId: Int)

    fun onCreateListener(view: View?)

    fun onCloseListener()
}