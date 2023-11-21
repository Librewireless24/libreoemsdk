package com.cumulations.libreV2.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.cumulations.libreV2.activity.CTSplashScreenActivity
import com.libreAlexa.R
import com.libreAlexa.databinding.CtFragmentTutorialsBinding
import com.libreAlexa.util.LibreLogger


class CTTutorialsFragment:Fragment() {
    val TAG = CTTutorialsFragment::class.java.simpleName
    private var binding: CtFragmentTutorialsBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            super.onCreateView(inflater, container, savedInstanceState)
        binding = CtFragmentTutorialsBinding.inflate(inflater, container, false)
        return binding!!.root
        //return inflater.inflate(R.layout.ct_fragment_tutorials,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding!!.webView.webViewClient = MyWebViewClient(binding!!.progressBar)

        binding!!.webView.settings.loadsImagesAutomatically = true
        binding!!.webView.settings.javaScriptEnabled = true
        binding!!.webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
//        binding!!.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        binding!!.webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        binding!!.webView.isScrollbarFadingEnabled = true
//        binding!!.webView.settings.userAgentString = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
        binding!!.webView.canGoBack()
        binding!!.webView.canGoForward()
        binding!!.webView.loadUrl(getString(R.string.riva_tutorial_url))

        binding!!.webView.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && keyEvent.action == MotionEvent.ACTION_UP
                    && binding!!.webView.canGoBack()) {
                binding!!.webView.goBack()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    internal class MyWebViewClient(var progressBar: ProgressBar) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

            LibreLogger.d("MyWebViewClient", "page started")
            progressBar.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            LibreLogger.d("MyWebViewClient", "page finished")
            progressBar.visibility = View.INVISIBLE
            super.onPageFinished(view, url)
        }

    }
}