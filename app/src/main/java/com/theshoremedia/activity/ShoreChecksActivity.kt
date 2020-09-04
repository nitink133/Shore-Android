package com.theshoremedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.theshoremedia.R
import com.theshoremedia.views.BubbleCredibilityCheckerView


class ShoreChecksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shore_checks)
        val text = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        BubbleCredibilityCheckerView.getInstance(this).init()
        finish()
    }
}
