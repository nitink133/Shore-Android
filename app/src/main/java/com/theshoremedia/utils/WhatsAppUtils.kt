package com.theshoremedia.utils

import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.theshoremedia.views.BubbleCredibilityCheckerView

/**
 * @author- Nitin Khanna
 * @date -
 */
object WhatsAppUtils {

    fun debugView(
        mContext: Context,
        rootNodeInfo: AccessibilityNodeInfo,
        parentView: AccessibilityNodeInfo? = null
    ) {

        var index = 0
        val count = rootNodeInfo.childCount
        var lastNodeInfo: AccessibilityNodeInfo? = null
        var nextNodeInfo: AccessibilityNodeInfo? = null
        while (index < count) {
            val currentNode = rootNodeInfo.getChild(index) ?: continue
            if (currentNode.childCount > 0) {
                debugView(mContext, currentNode, parentView = currentNode)
            } else if (parentView?.className == "android.view.ViewGroup" && currentNode.className == "android.widget.TextView") {
                BubbleCredibilityCheckerView.getInstance(mContext = mContext).init()
                Log.d("Nitin", lastNodeInfo?.className.toString())
                if (lastNodeInfo == null || nextNodeInfo == null) return
                if (lastNodeInfo.className.toString() != "android.widget.LinearLayout" || nextNodeInfo.className?.toString() != "android.widget.TextView") return
                if (!StringUtils.isTimeView(nextNodeInfo.text.toString())) return
                val text = currentNode.text.toString()
                if (StringUtils.isTimeView(text) || StringUtils.isDateView(text)) return
                Log.d("Nitin", index.toString())
                Log.d("Nitin", currentNode.className.toString())
                Log.d("Nitin", "ContentInfo: " + currentNode.text.toString())
//                BubbleCredibilityCheckerView.getInstance(mContext = mContext).init()
            }

            lastNodeInfo = currentNode
            index++
            nextNodeInfo =
                if (rootNodeInfo.childCount > index + 1) rootNodeInfo.getChild(index + 1) else null

        }
    }
}