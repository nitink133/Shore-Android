package com.theshoremedia.utils.extensions

import android.graphics.Color
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

fun debugView(
    rootNodeInfo: AccessibilityNodeInfo,
    parentView: AccessibilityNodeInfo? = null
) {

    var index = 0
    val count = rootNodeInfo.childCount
    var lastNodeInfo: AccessibilityNodeInfo? = null
    var nextNodeInfo: AccessibilityNodeInfo? = null
    while (index < count) {
        val currentNode = rootNodeInfo.getChild(index) ?: continue
        Log.d("Nitin", index.toString())

        Log.d("Nitin", currentNode.className.toString())
        if (currentNode.childCount > 0) {
            debugView(currentNode, parentView = currentNode)
        } else if (parentView?.className == "android.view.ViewGroup" && currentNode.className == "android.widget.TextView") {
//            Log.d("Nitin",lastNodeInfo?.className.toString())
            if (lastNodeInfo?.className.toString() != "android.widget.LinearLayout" && nextNodeInfo?.className?.toString() != "android.widget.TextView"
                && StringUtils.isTimeView(nextNodeInfo?.text.toString())
            ) return
            val text = currentNode.text.toString()
            if (StringUtils.isTimeView(text) || StringUtils.isDateView(text)) return

            Log.d("Nitin", "ContentInfo: " + currentNode.text.toString())
        }

        lastNodeInfo = currentNode
        index++
        nextNodeInfo = if (rootNodeInfo.childCount > index) rootNodeInfo.getChild(index) else null

    }
}

fun RecyclerView.validateNoDataView() {
    val counts = this.adapter?.itemCount ?: 0
    llNoData?.makeVisible(isVisible = counts == 0)

}