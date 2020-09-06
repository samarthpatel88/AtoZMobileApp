package com.atozkids.ui.activities

import BaseActivity
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.atozkids.R
import com.atozkids.data.DataManager
import com.atozkids.utils.Utility
import com.atozkids.utils.Utility.closeLoading
import com.atozkids.utils.Utility.showLoading
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : BaseActivity() {

    object Pages{
        const val CONTACT_US= "CONTACT_US"
        const val PRIVACY_POLICY= "PRIVACY_POLICY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        onInit()
    }

    private fun onInit() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled  = true
        webView.webViewClient = AppWebViewClient(this)

        if (DataManager.getMenuContentData() != null && intent.hasExtra(Pages.CONTACT_US)){
            webView.loadUrl(DataManager.getMenuContentData()!!.contactus)
        }else if (DataManager.getMenuContentData() != null && intent.hasExtra(Pages.PRIVACY_POLICY)){
            webView.loadUrl(DataManager.getMenuContentData()!!.privacypolicy)
        }else{
            showErrorMsg(getString(R.string.str_no_information_available))
        }
    }

    private class AppWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        private var isFirstTime: Boolean = true

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (isFirstTime) {
                isFirstTime = false
                showLoading(activity)
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            closeLoading()
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url: String = request?.url.toString()
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            Utility.showErrorToast(activity, error.toString())
        }
    }
}