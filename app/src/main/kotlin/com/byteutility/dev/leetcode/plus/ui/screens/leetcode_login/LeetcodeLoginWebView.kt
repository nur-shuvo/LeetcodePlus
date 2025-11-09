package com.byteutility.dev.leetcode.plus.ui.screens.leetcode_login

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private const val LEETCODE_LOGIN_URL = "https://leetcode.com/accounts/login"

/**
 * Created by Shuvo on 11/06/2025.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LeetCodeLoginWebView(
    onCookiesReceived: (cookieString: String) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                }

                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        val cookies = cookieManager.getCookie(url)
                        if (!cookies.isNullOrEmpty() && url == "https://leetcode.com/") {
                            Log.d(
                                "SHUVO-${this::class.java.simpleName}",
                                "onPageFinished: url: $url cookies: $cookies"
                            )
                            onCookiesReceived(cookies)
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return false
                    }
                }

                webChromeClient = WebChromeClient()
            }
        },
        update = { webView ->
            webView.loadUrl(LEETCODE_LOGIN_URL)
        },
        modifier = Modifier.fillMaxSize()
    )
}
