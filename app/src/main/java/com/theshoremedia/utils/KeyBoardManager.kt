package com.theshoremedia.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


object KeyBoardManager {
    fun hideKeyboard(activity: FragmentActivity?) {
        if (activity == null) return
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (view != null) {
            view.clearFocus()
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideKeyboard(activity: FragmentActivity?, view: View?) {
        val imm =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            view.clearFocus()
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showSoftKeyboard(activity: Activity) {
        val imm =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun keyboardVisibilityListener(
        activity: Activity?,
        lifecycleOwner: LifecycleOwner? = null,
        listener: ((isVisible: Boolean) -> Unit)
        ?
    ) {
        if (activity == null) return

        if (lifecycleOwner != null) {
            KeyboardVisibilityEvent.setEventListener(
                activity,
                lifecycleOwner,
                object : KeyboardVisibilityEventListener {
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        listener?.invoke(isOpen)
                    }
                })
        } else {
            KeyboardVisibilityEvent.setEventListener(
                activity,
                object : KeyboardVisibilityEventListener {
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        listener?.invoke(isOpen)
                    }
                })
        }
    }


}
