package com.theshoremedia.floatingview.credibility_checker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theshoremedia.views.BubbleCredibilityCheckerView

/**
 * @author- Nitin Khanna
 * @date -
 */
class CredibilityCheckerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
            intent.getStringExtra("reason") ?: return
            CredibilityCheckerService.getInstance()
                .removeBubbleView()
            BubbleCredibilityCheckerView.getInstance(context).close()
        }
    }
}