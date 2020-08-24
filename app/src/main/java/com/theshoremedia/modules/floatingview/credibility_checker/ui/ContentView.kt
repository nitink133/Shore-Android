package com.theshoremedia.modules.floatingview.credibility_checker.ui

import android.content.Context
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.modules.floatingview.credibility_checker.adapter.OverlayFactCheckAdapter
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.changeBackgroundColor
import com.theshoremedia.utils.extensions.validateNoDataView

class ContentView(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()


    private var recyclerView: RecyclerView? = null

    private var mAdapter =
        OverlayFactCheckAdapter(
            arrayListOf()
        ) { _, item ->
            CredibilityCheckerService.getInstance().rootView.showArticleView(item)
        }
    var layoutManager = LinearLayoutManager(context)

    init {
        inflate(context, R.layout.bubble_credibility_checker, this)

        recyclerView =
            findViewById<FrameLayout>(R.id.layoutRecycler)?.findViewById(R.id.recyclerView)
        layoutManager.stackFromEnd = true

        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = mAdapter
        FactCheckHistoryDatabaseHelper.instance?.getUnreadNews {
            mAdapter.addAll(items = it as ArrayList<FactCheckHistoryModel>)
            CredibilityCheckerService.getInstance().rootView.bubbleView?.notifications = it.size
            recyclerView?.validateNoDataView(
                findViewById<FrameLayout>(R.id.layoutRecycler)?.findViewById(
                    R.id.llNoData
                )
            )
            findViewById<View>(R.id.llRecyclerView).changeBackgroundColor(if (mAdapter.itemCount > 0) android.R.color.transparent else R.color.colorPrimary)
        }


        recyclerView?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //TODO
        }


        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }
        })
        scaleSpring.springConfig =
            SpringConfigs.CONTENT_SCALE

        scaleSpring.currentValue = 0.0
    }


    fun hideContent() {
        CredibilityCheckerService.getInstance().rootView.handler.removeCallbacks(
            CredibilityCheckerService.getInstance().rootView.showContentRunnable
        )

        scaleSpring.endValue = 0.0

        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

    fun showContent() {
        scaleSpring.endValue = 1.0

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }
}