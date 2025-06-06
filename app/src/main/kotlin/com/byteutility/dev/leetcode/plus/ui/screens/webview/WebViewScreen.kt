package com.byteutility.dev.leetcode.plus.ui.screens.webview

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.byteutility.dev.leetcode.plus.ui.common.ProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    onPopCurrent: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Leetcode Plus") },
                navigationIcon = {
                    IconButton(
                        onClick = { onPopCurrent.invoke() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFABDEF5).copy(
                        alpha = 0.1f
                    )
                )
            )
        },
    ) { innerPadding ->
        WebViewLayout(url, Modifier.padding(innerPadding))
    }
}

@Composable
fun WebViewLayout(url: String, modifier: Modifier = Modifier) {
    var progressIndicator by remember { mutableStateOf(true) }
    Box {
        Webview(url, modifier) {
            progressIndicator = it
        }
        if (progressIndicator) {
            ProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun Webview(
    url: String,
    modifier: Modifier,
    onProgressSet: (Boolean) -> Unit
) {
    AndroidView(factory = { context ->
        WebView(context).apply {
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
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                // TODO Commented now though this desktop mode works but very long width not fit in mobile screen, later we will go with this with correct configuration
                // setDesktopMode(true)
            }
            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    onProgressSet.invoke(true)
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onProgressSet.invoke(false)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
        }
    }, update = {
        it.loadUrl(url)
    },
        modifier = Modifier.then(modifier)
    )
}

fun WebView.setDesktopMode(enabled: Boolean) {
    val webSettings = getSettings()
    val newUserAgent = if (enabled) {
        webSettings.userAgentString.replace("Mobile", "eliboM").replace("Android", "diordnA")
    } else {
        webSettings.userAgentString.replace("eliboM", "Mobile").replace("diordnA", "Android")
    }

    webSettings.userAgentString = newUserAgent
    webSettings.useWideViewPort = true
    webSettings.loadWithOverviewMode = enabled
    webSettings.setSupportZoom(enabled)
    webSettings.builtInZoomControls = enabled
}

