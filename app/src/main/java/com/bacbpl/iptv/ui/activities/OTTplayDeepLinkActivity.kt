package com.bacbpl.iptv.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bacbpl.iptv.BuildConfig
import com.bacbpl.iptv.R
import com.bacbpl.iptv.utils.ToastUtils

class OTTplayDeepLinkActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var videoContainer: FrameLayout
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    private var webChromeClient: MyWebChromeClient? = null
    private var isPageLoaded = false
    private var targetUrl: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ottplay_deep_link)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        videoContainer = findViewById(R.id.videoContainer)

        setupOptimizedWebView()

        // Handle both Intent data and extras
        targetUrl = intent.dataString ?: intent.getStringExtra("url")

        Log.d("OTTplayDeepLink", "Target URL: $targetUrl")

        if (!targetUrl.isNullOrEmpty()) {
            loadUrlOptimized(targetUrl!!)
        } else {
            ToastUtils.showSafeToast(this, "No URL provided")
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupOptimizedWebView() {
        val webSettings = webView.settings

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.databaseEnabled = true

        // Enable mixed content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }

        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.mediaPlaybackRequiresUserGesture = false // Allow autoplay

        // Use hardware acceleration for better performance
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webChromeClient = MyWebChromeClient()
        webView.webChromeClient = webChromeClient

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                isPageLoaded = false
                Log.d("OTTplayDeepLink", "Page started: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                if (!isPageLoaded) {
                    injectVideoHandlerJS()
                    isPageLoaded = true
                }
                Log.d("OTTplayDeepLink", "Page finished: $url")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("OTTplayDeepLink", "Should override URL: $url")

                if (url.isNullOrEmpty()) return false

                // Handle ottplay:// scheme
                if (url.startsWith("ottplay://")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                            finish()
                            return true
                        }
                    } catch (e: Exception) {
                        Log.e("OTTplayDeepLink", "Error handling ottplay://", e)
                    }
                }

                // Load HTTP/HTTPS URLs in WebView
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    view?.loadUrl(url)
                    return true
                }

                return false
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("OTTplayDeepLink", "Error: $errorCode - $description")
                ToastUtils.showSafeToast(this@OTTplayDeepLinkActivity, "Error loading page: $description")
            }
        }

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            progressBar.progress = newProgress
            progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
        }

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            if (customView != null) {
                callback?.onCustomViewHidden()
                return
            }

            customView = view
            customViewCallback = callback

            webView.visibility = View.GONE
            videoContainer.visibility = View.VISIBLE
            videoContainer.addView(view)

            originalOrientation = requestedOrientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        override fun onHideCustomView() {
            customView?.let {
                videoContainer.removeView(it)
                customView = null
            }
            customViewCallback?.onCustomViewHidden()
            customViewCallback = null

            webView.visibility = View.VISIBLE
            videoContainer.visibility = View.GONE
            requestedOrientation = originalOrientation
        }

        override fun getVideoLoadingProgressView(): View? {
            return layoutInflater.inflate(R.layout.video_loading_progress, null)
        }
    }

    private fun injectVideoHandlerJS() {
        val jsCode = """
            javascript:(function() {
                try {
                    // Add video controls
                    var videos = document.getElementsByTagName('video');
                    for(var i = 0; i < videos.length; i++) {
                        videos[i].setAttribute('controls', 'true');
                        videos[i].setAttribute('playsinline', 'true');
                        videos[i].setAttribute('webkit-playsinline', 'true');
                    }
                    
                    // Make elements focusable for DPAD
                    var clickableElements = document.querySelectorAll('button, a, .play-button, [role="button"], .episode-item');
                    for(var i = 0; i < clickableElements.length; i++) {
                        clickableElements[i].setAttribute('tabindex', '0');
                        clickableElements[i].style.outline = 'none';
                    }
                    
                    // Add focus styles
                    var style = document.createElement('style');
                    style.textContent = '*:focus { outline: 2px solid red !important; }';
                    document.head.appendChild(style);
                    
                    console.log('Video handler injected successfully');
                } catch(e) {
                    console.log('Error: ' + e);
                }
            })();
        """.trimIndent()
        webView.loadUrl(jsCode)
    }

    private fun loadUrlOptimized(url: String) {
        try {
            Log.d("OTTplayDeepLink", "Loading URL: $url")
            webView.loadUrl(url)
        } catch (e: Exception) {
            Log.e("OTTplayDeepLink", "Error loading URL: ${e.message}")
            runOnUiThread {
                ToastUtils.showSafeToast(this@OTTplayDeepLinkActivity, "Error loading content")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                webView.loadUrl("javascript:document.activeElement?.click()")
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                webView.loadUrl("javascript:var v=document.querySelector('video');if(v){v.paused?v.play():v.pause()}")
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                if (customView != null) {
                    webChromeClient?.onHideCustomView()
                    return true
                }
                if (webView.canGoBack()) {
                    webView.goBack()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
        webView.loadUrl("javascript:document.querySelector('video')?.pause()")
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        try {
            if (customView != null) {
                webChromeClient?.onHideCustomView()
            }
            webView.stopLoading()
            webView.destroy()
        } catch (e: Exception) {
            Log.e("OTTplayDeepLink", "Error in onDestroy: ${e.message}")
        }
        super.onDestroy()
    }
}