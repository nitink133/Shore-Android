package com.theshoremedia.modules.floatingview.credibility_checker.ui

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.loadImage

/**
 * @author- Nitin Khanna
 * @date -
 */

class ArticleView(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()
    var isVisible: Boolean = false


    init {
        inflate(context, R.layout.bubble_article_view, this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            hide()
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


    fun hide() {
        isVisible = false

        CredibilityCheckerService.getInstance().rootView.handler.removeCallbacks(
            CredibilityCheckerService.getInstance().rootView.showArticleRunnable
        )

        scaleSpring.endValue = 0.0

        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

    fun show(item: FactCheckHistoryModel) {
        isVisible = true
        scaleSpring.endValue = 1.0

        findViewById<AppCompatTextView>(R.id.tvTitle).text = item.title
        findViewById<AppCompatTextView>(R.id.tvNewsSource).text = item.source
        findViewById<AppCompatTextView>(R.id.tvNewsDescription).text = item.description
        findViewById<AppCompatTextView>(R.id.tvNewsDate).text = item.date
        findViewById<ImageView>(R.id.ivNewsIcon).loadImage(item.icon)

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }
}