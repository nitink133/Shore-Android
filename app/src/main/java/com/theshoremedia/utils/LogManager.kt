package com.theshoremedia.utils


import android.util.Log
import com.theshoremedia.BuildConfig

/**
 * LogManager
 */
class LogManager {
    companion object {
        fun d(tag: String, message: String?) {
            if (BuildConfig.DEBUG) {
                if (message == null) {
                    return
                }
                Log.d(tag, message)
            }
        }

        fun e(tag: String, message: String) {
            if (BuildConfig.DEBUG) {
                Log.e(tag, message)
            }
        }

        fun v(tag: String = "WHO: ", message: String) {
            if (BuildConfig.DEBUG) {
                Log.v(tag, message)
            } else {
                // do nothing
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
}
