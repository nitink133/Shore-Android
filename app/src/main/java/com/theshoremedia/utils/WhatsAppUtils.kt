package com.theshoremedia.utils

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.theshoremedia.views.BubbleCredibilityCheckerView

/**
 * @author- Nitin Khanna
 * @date -
 */
class WhatsAppUtils {
    private var forwardedMessagesList: ArrayList<String> = arrayListOf()

    companion object {
        private var instance: WhatsAppUtils? = null
        private var mContext: Context? = null
        fun getInstance(context: Context): WhatsAppUtils {
            if (instance == null)
                instance = WhatsAppUtils().apply { mContext = context }
            return instance!!
        }
    }

    fun debugView(
        rootNodeInfo: AccessibilityNodeInfo,
        parentView: AccessibilityNodeInfo? = null
    ) {

        if (mContext == null) return
        var index = 0
        val count = rootNodeInfo.childCount
        var lastNodeInfo: AccessibilityNodeInfo? = null
        var nextNodeInfo: AccessibilityNodeInfo? = null

        if (rootNodeInfo.className == "android.widget.TextView") {
            if (forwardedMessagesList.contains(rootNodeInfo.text.toString())) return
        }

        while (index < count) {
            val currentNode = rootNodeInfo.getChild(index) ?: continue
            if (currentNode.childCount > 0) {
                debugView(currentNode, parentView = currentNode)
            } else if (parentView?.className == "android.view.ViewGroup" && currentNode.className == "android.widget.TextView") {
                if (lastNodeInfo == null || nextNodeInfo == null) return
                if (lastNodeInfo.className.toString() != "android.widget.LinearLayout" || nextNodeInfo.className?.toString() != "android.widget.TextView") return
                if (!StringUtils.isTimeView(nextNodeInfo.text.toString())) return
                val text = currentNode.text.toString()
                if (StringUtils.isTimeView(text) || StringUtils.isDateView(text)) return
                Log.d(
                    message = "Index: " + index.toString() + "/n ClassName: " + currentNode.className.toString() + "\n ContentInfo: " + currentNode.text.toString()
                )
                forwardedMessagesList.add(currentNode.text.toString())
                BubbleCredibilityCheckerView.getInstance(mContext = mContext!!).init()
            }

            lastNodeInfo = currentNode
            index++
            nextNodeInfo =
                if (rootNodeInfo.childCount > index + 1) rootNodeInfo.getChild(index + 1) else null

        }
    }
}