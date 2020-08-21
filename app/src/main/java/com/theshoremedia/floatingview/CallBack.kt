package com.theshoremedia.floatingview

import android.view.View

interface CallBack {

    fun onClickListener(resourceId: Int)

    fun onCreateListener(view: View?)

    fun onCloseListener()
}