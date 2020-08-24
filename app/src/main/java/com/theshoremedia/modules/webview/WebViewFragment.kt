package com.theshoremedia.modules.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.modules.base.BaseFragment


/**
 * A simple [Fragment] subclass.
 */
class WebViewFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_web_view, container, false)
        val mWebView = v.findViewById<View>(R.id.webView) as WebView
        mWebView.loadUrl("https://theshoremedia.com")

        // Enable Javascript
        val webSettings: WebSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.webViewClient = WebViewClient()
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WebViewFragment()
    }


    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle()
    }

}
