package com.theshoremedia.floatingview

interface FloatingLayoutContract {

    fun onCreate()

    fun onClose()

    fun create()

    fun close()

    fun isShow(): Boolean
}