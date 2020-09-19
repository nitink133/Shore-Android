package com.theshoremedia.modules.floatingview.credibility_checker.ui

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnLayout
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.ShareUtils
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.loadImage
import com.theshoremedia.utils.extensions.setFirstCharCapitalText


/**
 * @author- Nitin Khanna
 * @date -
 */

class ArticleView(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()
    var isVisible: Boolean = false
    private val llRootNewsView: LinearLayout
    private var mHeight: Int
    private var mWidth: Int


    init {
        inflate(context, R.layout.bubble_article_view, this)
        mHeight = height
        mWidth = width


        llRootNewsView = findViewById(R.id.llRootNewsView)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            hide()
        }

        findViewById<ImageView>(R.id.ivShare).setOnClickListener {
            ShareUtils.takeScreenshotAndShare(view = llRootNewsView)
        }

        doOnLayout {
            mWidth = it.measuredWidth
            mHeight = it.measuredHeight
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

        CredibilityCheckerService.getInstance().rootView.makeContentViewEnable()
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

        findViewById<AppCompatTextView>(R.id.tvTitle).text = item.articleTitle
        findViewById<AppCompatTextView>(R.id.tvNewsSource).setFirstCharCapitalText(item.sourceName)
        findViewById<AppCompatTextView>(R.id.tvNewsDescription).text = item.article
        findViewById<AppCompatTextView>(R.id.tvNewsDate).text = item.date
        findViewById<ImageView>(R.id.ivNewsIcon).loadImage(item.image)
        findViewById<AppCompatTextView>(R.id.tvForwardedMessage).text = item.forwardMessage

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        Log.d("Nitin", "Width: $mWidth\nHeight: $mHeight")
//        setMeasuredDimension(mWidth, mHeight)
//    }


}