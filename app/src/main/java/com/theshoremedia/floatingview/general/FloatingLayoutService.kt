package com.theshoremedia.floatingview.general

/**
 * @author- Nitin Khanna
 * @date -
 */

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.ResultReceiver
import android.view.*
import android.view.View.OnTouchListener
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.FloatingViewsLayoutParamsUtils
import java.util.*


class FloatingLayoutService : Service(), View.OnClickListener {

    private var mContext: Context? = null
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var mReceiver: ResultReceiver? = null
    private var mRootContainer: View? = null
    private var mWindowManager: WindowManager? = null

    @LayoutRes
    private var mResource = 0

    private var mLayoutMovable = true
    private var mFloatingViewType = 0

    @IdRes
    private var ROOT_CONTAINER_ID = 0

    companion object {
        var mFloatingView: View? = null

        const val LAYOUT_RESOURCE = "layout-resource"
        const val LAYOUT_MOVABLE = "layout-movable"
        const val LAYOUT_VIEW_TYPE = "layout-view-type"
        const val RECEIVER = "receiver"
        const val ACTION_ON_CLICK = 3265
        const val ACTION_ON_CREATE = 5874
        const val ACTION_ON_CLOSE = 3625
        const val ID = "id"
    }

    override fun onBind(p0: Intent?): IBinder? {
        //To change body of created functions use File | Settings | File Templates.
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        mHandler = Handler()
        ROOT_CONTAINER_ID = resources.getIdentifier("root_container", "id", packageName)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        if (intent == null) return

        mResource = intent.getIntExtra(LAYOUT_RESOURCE, 0)
        mLayoutMovable = intent.getBooleanExtra(LAYOUT_MOVABLE, true)
        mFloatingViewType = intent.getIntExtra(LAYOUT_VIEW_TYPE, 0)
        mReceiver = intent.getParcelableExtra(RECEIVER) as? ResultReceiver

        onDestroyView()
        createView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mContext != null) mContext = null
        if (mHandler != null) mHandler = null
        if (mRunnable != null) mRunnable = null

        onClose()
        onDestroyView()
    }

    private fun createView() {
        val params: WindowManager.LayoutParams = when (mFloatingViewType) {
            AppConstants.FloatingViewType.ACCESSIBILITY_HELP -> FloatingViewsLayoutParamsUtils.getDefaultParams(
                gravity = Gravity.TOP or Gravity.END,
                verticalMargin = .2F
            )
            else -> FloatingViewsLayoutParamsUtils.getDefaultParams()
        }
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mFloatingView = layoutInflater.inflate(mResource, null)
        mWindowManager?.addView(mFloatingView, params)
        mRootContainer = mFloatingView?.findViewById(ROOT_CONTAINER_ID)

        setMoveView(params)
        val resultData = Bundle()
        mReceiver?.send(ACTION_ON_CREATE, resultData)
    }

    private fun setMoveView(params: WindowManager.LayoutParams) {
        if (mRootContainer == null) return
        if (mLayoutMovable)
            mRootContainer!!.setOnTouchListener(object : OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            //remember the initial position.
                            initialX = params.x
                            initialY = params.y
                            //get the touch location
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            //Update the layout with new X & Y coordinate
                            mWindowManager?.updateViewLayout(mFloatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
        setOnClickToView(mRootContainer!!)
    }

    private fun setOnClickToView(view: View) {
        if (view is ViewGroup) {
            for (idx in 0 until view.childCount) {
                setOnClickToView(view.getChildAt(idx))
            }
        } else if (view.tag is String) {
            val tag = view.tag.toString().toLowerCase(Locale.ROOT)
            if ("click" == tag) view.setOnClickListener(this)
        }
    }

    private fun onDestroyView() {
        if (mWindowManager != null)
            mWindowManager!!.removeView(mFloatingView)
    }

    private fun onClose() {
        val resultData = Bundle()
        mReceiver?.send(ACTION_ON_CLOSE, resultData)
    }

    override fun onClick(v: View) {
        val resultData = Bundle()
        resultData.putInt(ID, v.id)
        mReceiver?.send(ACTION_ON_CLICK, resultData)
    }

}