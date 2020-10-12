package com.theshoremedia.modules.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.databinding.FragmentWebViewBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.webview.WebViewFragmentArgs.fromBundle
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.extensions.makeVisible


/**
 * A simple [Fragment] subclass.
 */
class WebViewFragment : BaseFragment() {
    private lateinit var binding: FragmentWebViewBinding
    private val type: Int by lazy {
        fromBundle(arguments!!).type
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Enable Javascript
        val settings = binding.webView.settings

        settings.javaScriptEnabled = true
        binding.webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        binding.progressBar.makeVisible(isVisible = true)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progressBar.makeVisible(isVisible = false)
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                ToastUtils.makeToast(mContext, "Error:$description")

            }
        }
        when (type) {
            AppConstants.NavigationItem.PRIVACY_POLICY -> binding.webView.loadUrl(
                getString(
                    R.string.url_privacy_policy
                )
            )
            AppConstants.NavigationItem.HELP_SUPPORT -> binding.webView.loadUrl(
                getString(
                    R.string.url_help
                )
            )
            AppConstants.NavigationItem.ABOUT_US -> binding.webView.loadUrl(getString(R.string.url_about_us))
        }

    }


    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle()
    }

}
