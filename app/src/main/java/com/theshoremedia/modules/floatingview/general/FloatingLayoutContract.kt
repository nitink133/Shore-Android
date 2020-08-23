package com.theshoremedia.modules.floatingview.general

interface FloatingLayoutContract {

    fun onCreate()

    fun onClose()

    fun create()

    fun close()

    fun isShow(): Boolean
}