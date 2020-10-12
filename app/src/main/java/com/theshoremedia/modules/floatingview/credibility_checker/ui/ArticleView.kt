package com.theshoremedia.modules.floatingview.credibility_checker.ui

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.SourcesDatabaseHelper
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.ShareUtils
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.loadImage
import com.theshoremedia.utils.extensions.setFirstCharCapitalText
import com.theshoremedia.utils.permissions.StoragePermissionsUtils


/**
 * @author- Nitin Khanna
 * @date -
 */

class ArticleView(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()
    var isVisible: Boolean = false
    private val llRootNewsView: LinearLayout


    init {
        inflate(context, R.layout.bubble_article_view, this)


        llRootNewsView = findViewById(R.id.llRootNewsView)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            hide()
        }

        findViewById<ImageView>(R.id.ivShare).setOnClickListener {
            StoragePermissionsUtils.verifyPermission(context) {
                if (it) {
                    CredibilityCheckerService.getInstance().rootView.collapse()

                    findViewById<TextView>(R.id.tvNewsDescription).maxLines = 20
                    ShareUtils.takeScreenshotAndShare(llRootNewsView) {
                        findViewById<TextView>(R.id.tvNewsDescription).maxLines = Integer.MAX_VALUE
                    }
                } else {
                    ToastUtils.makeToast(
                        context,
                        context.getString(R.string.permission_storage_error)
                    )
                }
            }
        }

        val tvMoreForwardedMsg = findViewById<TextView>(R.id.tvMoreForwardedMsg)
        val tvForwardedMessage = findViewById<TextView>(R.id.tvForwardedMessage)
        val tvAboutSourceMore = findViewById<TextView>(R.id.tvAboutSourceMore)
        val tvAboutTheSource = findViewById<TextView>(R.id.tvAboutTheSource)

        tvMoreForwardedMsg.setOnClickListener {
            val isExpended: Boolean = tvMoreForwardedMsg.tag as? Boolean ?: false
            tvMoreForwardedMsg.tag = !isExpended
            if (!isExpended) {
                tvMoreForwardedMsg.text = context.getString(R.string.lbl_less)
                tvForwardedMessage.maxLines = Integer.MAX_VALUE
            } else {
                tvMoreForwardedMsg.text = context.getString(R.string.lbl_more)
                tvForwardedMessage.maxLines = 4
            }
        }
        tvAboutSourceMore.setOnClickListener {
            val isExpended: Boolean = tvAboutSourceMore.tag as? Boolean ?: false
            tvAboutSourceMore.tag = !isExpended
            if (!isExpended) {
                tvAboutSourceMore.text = context.getString(R.string.lbl_less)
                tvAboutTheSource.maxLines = Integer.MAX_VALUE
            } else {
                tvAboutSourceMore.text = context.getString(R.string.lbl_more)
                tvAboutTheSource.maxLines = 4
            }
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


        SourcesDatabaseHelper.instance?.getSourceInfo(item.sourceName) {
            item.aboutSource = it
            llRootNewsView.setTag(R.string.key_model, item)
            findViewById<AppCompatTextView>(R.id.tvAboutTheSource).text = item.aboutSource?.about
        }


        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }


//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension(widthMeasureSpec, resolveSize(800, heightMeasureSpec));
//    }
}