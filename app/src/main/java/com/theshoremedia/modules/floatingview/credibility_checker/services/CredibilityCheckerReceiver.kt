package com.theshoremedia.modules.floatingview.credibility_checker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theshoremedia.utils.AppConstants

/**
 * @author- Nitin Khanna
 * @date -
 */
class CredibilityCheckerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        //TODO: There is an issue, related to System dialog closing. Need to check this with backgronud service calling scenario.
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
            intent.getStringExtra("reason") ?: return
            if (CredibilityCheckerService.isInitialized)
                CredibilityCheckerService.getInstance().rootView.collapse()

            return
        }

        val requestCode = intent.getIntExtra(AppConstants.Key.REQUEST_CODE, 0)
        if (requestCode == 1001) {
            if (CredibilityCheckerService.isInitialized)
                CredibilityCheckerService.getInstance().removeBubbleView()
        }
    }
}
