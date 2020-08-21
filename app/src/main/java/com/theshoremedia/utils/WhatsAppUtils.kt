package com.theshoremedia.utils

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

/**
 * @author- Nitin Khanna
 * @date -
 */
object WhatsAppUtils {

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
            nextNodeInfo =
                if (rootNodeInfo.childCount > index) rootNodeInfo.getChild(index) else null

        }
    }
}