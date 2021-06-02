package com.jianzhi.glitter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.orange.glitter.R
import kotlinx.android.synthetic.main.glitter_page.view.*

class WebViewAct : AppCompatActivity(){

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_page)
        val rootview = findViewById<View>(android.R.id.content).rootView
        findViewById<WebView>(R.id.webroot).loadUrl(intent.getStringExtra("url"))
        findViewById<TextView>(R.id.link).setText(intent.getStringExtra("url"))
        findViewById<ImageView>(R.id.backimg).setOnClickListener {
            finish()
        }
        rootview.webroot.settings.allowUniversalAccessFromFileURLs = true
        rootview.webroot.settings.javaScriptEnabled = true
        rootview.webroot.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                Log.e("OverrideUrlLoading", url)
                return false
            }
            override fun onPageFinished(view: WebView?, url: String?) {
//                rootview.webroot.evaluateJavascript(
//                    "glitter.baseUrl='${GlitterActivity.baseRout}/';glitter.type = appearType.Android;onCreate();",
//                    null
//                )
                super.onPageFinished(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                Log.e("shouldInterceptRequest", "" + request.url)
                return super.shouldInterceptRequest(view, request)
            }
        }
    }
}