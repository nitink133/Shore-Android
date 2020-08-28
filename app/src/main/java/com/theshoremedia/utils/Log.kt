package com.theshoremedia.utils


import android.util.Log
import com.orhanobut.logger.Logger
import com.theshoremedia.BuildConfig

/**
 * LogManager
 */
object Log {
    fun d(tag: String = "", message: String?) {
        if (BuildConfig.DEBUG) {
            if (message == null) {
                return
            }
            if (tag.isNotEmpty()) {
                Log.d(tag, message)
                return
            }
            Logger.d(message)
        }
    }

    fun e(tag: String = "", message: String) {
        if (BuildConfig.DEBUG) {
            if (tag.isNotEmpty()) {
                Log.e(tag, message)
                return
            }
            Logger.e(message)
        }
    }

    fun v(tag: String = "WHO: ", message: String) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    fun w(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message)
        }
    }

    /*
     *Method used to print stacktrace
     */
    @JvmStatic
    fun printStackTrace(e: Throwable?) {
        e?.printStackTrace()
    }
}
