package com.theshoremedia

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.theshoremedia.utils.PreferenceUtils


/**
 * @author- Nitin Khanna
 * @date -
 */
class AppController : Application() {
    companion object {

        private var instance: AppController? = null
        fun getInstance(): AppController {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        PreferenceUtils.init(this)
        Logger.addLogAdapter(AndroidLogAdapter())
    }


}