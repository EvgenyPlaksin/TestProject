package com.example.testproject.webview.client

import android.app.Activity
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.testproject.game.fragments.utils.Variables.url_2


// если  уже делать красиво то надо бы по разным классам запихнуть
class WebClient internal constructor(private val activity: Activity) : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        view?.loadUrl(request?.url.toString())
        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url?.startsWith("tel:") == true || url?.startsWith("mailto:") == true || url?.startsWith(
                "tg:"
            ) == true
        ) {
            return true
        } else {
            if (url != null) {
                view?.loadUrl(url)
            }
        }
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.e("onPageFinished", "$url")
        CookieManager.getInstance().flush()
        url_2 = url
    }
}