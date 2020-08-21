package com.theshoremedia.floatingview

/**
 * @author- Nitin Khanna
 * @date -
 */

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.Gravity
import androidx.annotation.LayoutRes

class FloatingLayout(
    private val mContext: Context,
    @field:LayoutRes @LayoutRes private var mResource: Int,
    private var mFloatingViewType: Int = Gravity.CENTER,
    private var mLayoutMovable: Boolean = true,
    private var callBack: CallBack
) : FloatingLayoutContract {

    private var isShow = false

    override fun onCreate() {
        val intent = Intent(mContext, FloatingLayoutService::class.java)
        intent.putExtra(FloatingLayoutService.LAYOUT_RESOURCE, mResource)
        intent.putExtra(FloatingLayoutService.LAYOUT_VIEW_TYPE, mFloatingViewType)
        intent.putExtra(FloatingLayoutService.LAYOUT_MOVABLE, mLayoutMovable)
        intent.putExtra(FloatingLayoutService.RECEIVER, ServiceReceiver(Handler(), callBack))

        mContext.startService(intent)
    }

    override fun onClose() {
        mContext.stopService(Intent(mContext, FloatingLayoutService::class.java))
    }

    override fun create() {
        isShow = true
        onCreate()
    }

    override fun close() {
        isShow = false
        onClose()
    }

    override fun isShow(): Boolean {
        return isShow
    }

}