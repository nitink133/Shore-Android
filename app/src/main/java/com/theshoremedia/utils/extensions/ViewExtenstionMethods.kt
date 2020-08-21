package com.theshoremedia.utils.extensions

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theshoremedia.modules.base.BaseCustomDialog
import com.theshoremedia.utils.StringUtils
import kotlinx.android.synthetic.main.layout_recycler_view.view.*

/**
 * @author- Nitin Khanna
 * @date -
 */

fun View.makeVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.makeVisibleWithAnimation(isVisible: Boolean) {
    makeVisible(isVisible)
    //TODO: Fix animation related issue
//    if (isVisible) this.showWithAnimation() else this.hideWithAnimation()
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = Color.BLUE
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}


fun RecyclerView.validateNoDataView() {
    val counts = this.adapter?.itemCount ?: 0
    llNoData?.makeVisible(isVisible = counts == 0)
}

fun showCustomDialog(
    context: Context,
    view: View,
    func: BaseCustomDialog.() -> Unit
): AlertDialog = BaseCustomDialog(context, view).apply {
    func()
}.create()

