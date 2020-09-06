package com.theshoremedia.utils.whatsapp

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.theshoremedia.modules.floatingview.credibility_checker.model.ValidateNewsReqModel
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.StringUtils
import com.theshoremedia.views.BubbleCredibilityCheckerView

/**
 * @author- Nitin Khanna
 * @date -
 */
class WhatsAppUtils {
    var forwardedMessagesList: ArrayList<ValidateNewsReqModel> = arrayListOf()

    companion object {
        private var instance: WhatsAppUtils? = null
        var mContext: Context? = null
        fun getInstance(context: Context): WhatsAppUtils {
            if (instance == null)
                instance = WhatsAppUtils()
                    .apply { mContext = context }
            return instance!!
        }

        fun getInstance(): WhatsAppUtils? {
            return instance
        }
    }

    fun processScreenCallbacks(
        rootNodeInfo: AccessibilityNodeInfo
    ) {
        debugView(rootNodeInfo = rootNodeInfo)
    }

    private fun debugView(
        rootNodeInfo: AccessibilityNodeInfo,
        parentView: AccessibilityNodeInfo? = null
    ) {

        if (mContext == null) return
        var index = 0
        val count = rootNodeInfo.childCount
        var lastNodeInfo: AccessibilityNodeInfo? = null
        var nextNodeInfo: AccessibilityNodeInfo? = null


        Log.d("Shore", "debugView")
        while (index < count) {
            val currentNode = rootNodeInfo.getChild(index) ?: continue
            Log.d("Shore", "Current View: ${currentNode.className}\nIndex: $index")
            if (currentNode.childCount > 0) {
                debugView(currentNode, parentView = currentNode)
            } else if (parentView?.className == "android.view.ViewGroup" && currentNode.className == "android.widget.TextView") {

                if (lastNodeInfo == null || nextNodeInfo == null) return
                if (lastNodeInfo.className.toString() != "android.widget.LinearLayout" || nextNodeInfo.className?.toString() != "android.widget.TextView") return
                if (!StringUtils.isTimeView(nextNodeInfo.text.toString())) return
                val text = currentNode.text.toString()
                if (lastNodeInfo.childCount > 0) return
                if (StringUtils.isTimeView(text) || StringUtils.isDateView(
                        text
                    )
                ) return
                if (currentNode.text.isNullOrEmpty()) return

                Log.d(
                    message = "Index: " + index.toString() + "\n ClassName: " + currentNode.className.toString() + "\n ContentInfo: " + currentNode.text.toString()
                            + "\n Next Node Info: " + nextNodeInfo.className.toString() + "\n Last Node Info: " + lastNodeInfo.className.toString()
                            + "\n Last Node Child Info: " + lastNodeInfo.childCount
                )
                addToListAndShowBubble(currentNode.text!!.toString())
            }

            lastNodeInfo = currentNode
            index++
            nextNodeInfo =
                if (rootNodeInfo.childCount > index + 1) rootNodeInfo.getChild(index + 1) else null

        }
    }

    private fun addToListAndShowBubble(message: String) {
        var isAlreadyExist = false
        repeat(forwardedMessagesList.size) lit@{
            if (forwardedMessagesList[it].query == message) {
                isAlreadyExist = true
                return@lit
            }
        }
        if (!isAlreadyExist) {
            forwardedMessagesList.add(ValidateNewsReqModel(query = message))
            BubbleCredibilityCheckerView.getInstance(mContext = mContext!!).init(isNewData = true)
            return
        }
        BubbleCredibilityCheckerView.getInstance(mContext = mContext!!).init()
    }


    fun debugView() {
        addToListAndShowBubble(message = AppConstants.DummyData.FORWARD_MESSAGE)
    }
}