package com.theshoremedia.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.theshoremedia.R
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.PreferenceUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        val timeInMil = 2000L
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(
                this,
                if (PreferenceUtils.getPref<Boolean>(AppConstants.Preference.IS_FIRST_TIME_USER) != false
                ) IntroScreenActivity::class.java
                else MainActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            finish()
        }, timeInMil)
    }
}
