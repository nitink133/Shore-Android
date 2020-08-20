package com.theshoremedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.theshoremedia.R
import com.theshoremedia.R.*
import com.theshoremedia.adapter.DynamicViewPagerAdapter
import com.theshoremedia.databinding.ActivityIntroScreenBinding
import com.theshoremedia.utils.extensions.*


class IntroScreenActivity : AppCompatActivity() {
    private var mWelcomePage1: ViewGroup? = null
    private var mWelcomePage2: ViewGroup? = null
    private var mWelcomePage3: ViewGroup? = null
    private var mWelcomePage4: ViewGroup? = null

    private lateinit var binding: ActivityIntroScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layout.activity_intro_screen)
        val pagerAdapter = DynamicViewPagerAdapter()
        addPagerViews(pagerAdapter)
        binding.ivLeft.setOnClickListener {
            if (binding.viewPager.currentItem - 1 >= 0) binding.viewPager.setCurrentItem(
                binding.viewPager.currentItem - 1,
                true
            )
        }
        binding.ivRight.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < pagerAdapter.count) binding.viewPager.currentItem =
                binding.viewPager.currentItem + 1 else { // end presentation
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.ivRight.makeVisibleWithAnimation(isVisible = position + 1 != pagerAdapter.count)
                binding.ivLeft.makeVisibleWithAnimation(isVisible = position != 0)
            }

            override fun onPageSelected(position: Int) {
                //
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.viewPager.adapter = pagerAdapter
    }

    private fun addPagerViews(pagerAdapter: DynamicViewPagerAdapter) {
        run {
            mWelcomePage1 =
                layoutInflater.inflate(layout.layout_intro_screen, null, false) as ViewGroup
            mWelcomePage1?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.demo_whatsapp_chat)
            mWelcomePage1?.findViewById<TextView>(id.tvFooter)?.text =
                getString(R.string.intro_screen_1)
            pagerAdapter.addView(mWelcomePage1)
        }
        run {
            mWelcomePage2 =
                layoutInflater.inflate(layout.layout_intro_screen, null, false) as ViewGroup
            mWelcomePage2?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.demo_checkout_cred)

            mWelcomePage2?.findViewById<TextView>(id.tvFooter)?.text =
                getString(R.string.intro_screen_2)
            pagerAdapter.addView(mWelcomePage2)
        }
        run {
            mWelcomePage3 =
                layoutInflater.inflate(layout.layout_intro_screen, null, false) as ViewGroup
            mWelcomePage3?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.demo_article)

            mWelcomePage3?.findViewById<TextView>(id.tvFooter)?.text =
                getString(R.string.intro_screen_3)
            pagerAdapter.addView(mWelcomePage3)
        }
        run {
            mWelcomePage4 =
                layoutInflater.inflate(
                    layout.layout_intro_screen,
                    null,
                    false
                ) as ViewGroup
            mWelcomePage4?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.demo_accessibility)
            mWelcomePage4?.findViewById<TextView>(id.tvDescription)?.makeVisible(isVisible = true)
            mWelcomePage4?.findViewById<TextView>(id.tvDescription)?.text =
                getString(string.enable_accessibility)

            mWelcomePage4?.findViewById<TextView>(id.tvFooter)?.text =
                getString(R.string.intro_screen_4)

            mWelcomePage4?.findViewById<TextView>(id.tvFooter)?.makeLinks(
                Pair(getString(R.string.txt_activate_not), View.OnClickListener {
                    //Create ToolTip for accessibility services.
                    startActivity(Intent(this@IntroScreenActivity,MainActivity::class.java))
                })
            )
            pagerAdapter.addView(mWelcomePage4)
        }

    }

    override fun onResume() {
        super.onResume()
        slideView(mWelcomePage1)
        slideView(mWelcomePage2)
        slideView(mWelcomePage3)
        slideView(mWelcomePage4)
    }


    private fun slideView(viewGroup: ViewGroup?) {
        if (viewGroup == null) return
        viewGroup.findViewById<View>(id.imageView)?.animateFromBottomCentered()
        viewGroup.findViewById<View>(id.textView)?.animateFromBottom()
    }

    companion object {
        val TAG = IntroScreenActivity::class.java.simpleName
    }


}
