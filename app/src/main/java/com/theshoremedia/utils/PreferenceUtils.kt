package com.theshoremedia.utils

import android.content.Context
import android.content.SharedPreferences
import com.theshoremedia.BuildConfig

/**
 * @author- Nitin Khanna
 * @date -
 */


object PreferenceUtils {
    private val TAG = PreferenceUtils::class.java.simpleName

    private var pref: SharedPreferences? = null

    fun init(context: Context) {
        pref = if (pref == null) {
            context.getSharedPreferences(
                BuildConfig.APPLICATION_ID,
                Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS
            )
        } else {
            throw RuntimeException("Double init our preference")
        }
    }

    private fun getEditor(): SharedPreferences.Editor {
        return pref!!.edit()
    }

    fun savePref(key: String?, value: Any?) {
        val editor = getEditor()
        when {
            value is Boolean -> {
                editor.putBoolean(key, (value as Boolean?)!!)
            }
            value is Int -> {
                editor.putInt(key, (value as Int?)!!)
            }
            value is Float -> {
                editor.putFloat(key, (value as Float?)!!)
            }
            value is Long -> {
                editor.putLong(key, (value as Long?)!!)
            }
            value is String -> {
                editor.putString(key, value as String?)
            }
            value is Enum<*> -> {
                editor.putString(key, value.toString())
            }
            value != null -> {
                throw RuntimeException("Attempting to save non-primitive preference")
            }
        }
        editor.commit()
    }

    fun <T> getPref(key: String?): T? {
        return pref!!.all[key] as T?
    }

    fun <T> getPref(key: String?, defValue: T): T {
        val returnValue = pref!!.all[key] as T?
        return returnValue ?: defValue
    }

    fun remove(key: String?) {
        val editor = getEditor()
        editor.remove(key)
        editor.commit()
    }

    fun clear() {
        val editor = getEditor()
        editor.clear()
        editor.commit()
    }

}