package com.jianzhi.glitter

import android.graphics.Rect
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.webkit.WebView

class HeightVisibleChangeListener(private val webview: WebView) : OnGlobalLayoutListener {
    var lastHeight = 0
    var lastVisibleHeight = 0
    override fun onGlobalLayout() {
        val visible = Rect()
        val size = Rect()
        webview.getWindowVisibleDisplayFrame(visible)
        webview.getHitRect(size)
        val height = size.bottom - size.top
        val visibleHeight = visible.bottom - visible.top
        if (height == lastHeight && lastVisibleHeight == visibleHeight) return
        lastHeight = height
        lastVisibleHeight = visibleHeight
        val js = "javascript:glitter.KeyboardUtil.OnHeightChange($height,$visibleHeight)"
        webview.loadUrl(js)
    }

    init {
        webview.viewTreeObserver.addOnGlobalLayoutListener(this)
    }
}