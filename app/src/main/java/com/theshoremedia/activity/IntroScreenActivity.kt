package com.theshoremedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.theshoremedia.R
import com.theshoremedia.R.*
import com.theshoremedia.databinding.ActivityIntroScreenBinding
import com.theshoremedia.modules.base.adapter.IntroViewPagerAdapter
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.PreferenceUtils
import com.theshoremedia.utils.extensions.animateFromBottom
import com.theshoremedia.utils.extensions.animateFromBottomCentered
import com.theshoremedia.utils.extensions.loadImage
import com.theshoremedia.utils.extensions.makeVisibleWithAnimation

class IntroScreenActivity : AppCompatActivity() {
    private var mWelcomePage1: ViewGroup? = null
    private var mWelcomePage2: ViewGroup? = null
    private var mWelcomePage3: ViewGroup? = null
    private var mWelcomePage4: ViewGroup? = null

    private lateinit var binding: ActivityIntroScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layout.activity_intro_screen)
        val pagerAdapter = IntroViewPagerAdapter()
        addPagerViews(pagerAdapter)
        binding.ivLeft.setOnClickListener {
            if (binding.viewPager.currentItem - 1 >= 0) binding.viewPager.setCurrentItem(
                binding.viewPager.currentItem - 1,
                true
            )
        }

        binding.btnGetStarted.setOnClickListener {
            moveToMain()
        }

        binding.tvSkip.setOnClickListener {
            moveToMain()
        }
        binding.ivRight.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < pagerAdapter.count) binding.viewPager.currentItem =
                binding.viewPager.currentItem + 1 else { // end presentation
                moveToMain()
            }
        }
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position + 1 == pagerAdapter.count) {
                    binding.ivLeft.makeVisibleWithAnimation(isVisible = false)
                    binding.ivRight.makeVisibleWithAnimation(isVisible = false)
                    binding.btnGetStarted.makeVisibleWithAnimation(isVisible = position + 1 == pagerAdapter.count)
                    binding.lllogoHeaderSkip.makeVisibleWithAnimation(isVisible = false)
                    binding.llLogoView.makeVisibleWithAnimation(isVisible = true)
                    return
                }

                binding.ivRight.makeVisibleWithAnimation(isVisible = position + 1 != pagerAdapter.count)
                binding.ivLeft.makeVisibleWithAnimation(isVisible = position != 0)
                binding.btnGetStarted.makeVisibleWithAnimation(isVisible = false)
                binding.lllogoHeaderSkip.makeVisibleWithAnimation(isVisible = true)
                binding.llLogoView.makeVisibleWithAnimation(isVisible = false)

            }

            override fun onPageSelected(position: Int) {
                //
            }

            override fun onPageScrollStateChanged(state: Int) {
               //no use
            }
        })
        binding.viewPager.adapter = pagerAdapter
    }

    private fun moveToMain() {
        PreferenceUtils.savePref(AppConstants.Preference.IS_FIRST_TIME_USER, false)
        val intent = Intent(this@IntroScreenActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun addPagerViews(pagerAdapter: IntroViewPagerAdapter) {
        run {
            mWelcomePage1 =
                layoutInflater.inflate(layout.layout_info_screen, null, false) as ViewGroup
            mWelcomePage1?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.ic_info_confused)
            mWelcomePage1?.findViewById<TextView>(id.tvHeader)?.text =
                getText(R.string.lbl_intro_1)
            mWelcomePage1?.findViewById<TextView>(id.tvDescription)?.text =
                getText(R.string.des_intro_1)
            pagerAdapter.addView(mWelcomePage1)
        }
        run {
            mWelcomePage2 =
                layoutInflater.inflate(layout.layout_info_screen, null, false) as ViewGroup
            mWelcomePage2?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.ic_info_read)

            mWelcomePage2?.findViewById<TextView>(id.tvHeader)?.text =
                getText(R.string.lbl_intro_2)
            mWelcomePage2?.findViewById<TextView>(id.tvDescription)?.text =
                getText(R.string.des_intro_2)
            pagerAdapter.addView(mWelcomePage2)
        }
        run {
            mWelcomePage3 =
                layoutInflater.inflate(layout.layout_info_screen, null, false) as ViewGroup
            mWelcomePage3?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.ic_info_peace)

            mWelcomePage3?.findViewById<TextView>(id.tvHeader)?.text =
                getText(R.string.lbl_intro_3)
            mWelcomePage3?.findViewById<TextView>(id.tvDescription)?.text =
                getText(R.string.des_intro_3)
            pagerAdapter.addView(mWelcomePage3)
        }
        run {
            mWelcomePage4 =
                layoutInflater.inflate(
                    layout.layout_info_screen,
                    null,
                    false
                ) as ViewGroup
            mWelcomePage4?.findViewById<AppCompatImageView>(id.imageView)
                ?.loadImage(drawable.ic_info_privacy)

            mWelcomePage4?.findViewById<TextView>(id.tvHeader)?.text =
                getText(R.string.lbl_intro_4)
            mWelcomePage4?.findViewById<TextView>(id.tvDescription)?.text =
                getText(R.string.des_intro_4)
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
