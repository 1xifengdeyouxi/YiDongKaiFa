package com.swu.webviewandjavascript

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var webView: WebView
    private lateinit var btnCallJs: Button
    private lateinit var btnShowToast: Button
    private lateinit var btnReload: Button
    private lateinit var tvStatus: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupWebView()
        loadHtmlPage()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        webView = findViewById(R.id.web_view)
        btnCallJs = findViewById(R.id.btn_call_js)
        btnShowToast = findViewById(R.id.btn_show_toast)
        btnReload = findViewById(R.id.btn_reload)
        tvStatus = findViewById(R.id.tv_status)
        
        btnCallJs.setOnClickListener(this)
        btnShowToast.setOnClickListener(this)
        btnReload.setOnClickListener(this)
    }
    
    /**
     * 设置WebView
     */
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setupWebView() {
        val webSettings = webView.settings
        
        // 启用JavaScript
        webSettings.javaScriptEnabled = true
        
        // 支持缩放
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        
        // 允许访问文件
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        
        // 设置WebViewClient
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                tvStatus.text = "正在加载页面..."
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                tvStatus.text = "页面加载完成"
            }
            
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                tvStatus.text = "加载错误: ${error?.description}"
                Toast.makeText(this@MainActivity, "加载失败: ${error?.description}", Toast.LENGTH_SHORT).show()
            }
        }
        
        // 设置WebChromeClient
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) {
                    tvStatus.text = "加载中: $newProgress%"
                }
            }
        }
        
        // 添加 JavaScript 接口，供 JavaScript 调用 Android 方法
        webView.addJavascriptInterface(AndroidInterface(), "AndroidInterface")
    }
    
    /**
     * 加载HTML页面
     */
    private fun loadHtmlPage() {
        try {
            // 从 assets 目录加载 HTML 文件
            webView.loadUrl("file:///android_asset/index.html")
        } catch (e: Exception) {
            Toast.makeText(this, "加载页面失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_call_js -> {
                callJavaScript()
            }
            R.id.btn_show_toast -> {
                // 演示：通过 JavaScript 调用 Android 方法
                webView.evaluateJavascript("callAndroidToast();", null)
            }
            R.id.btn_reload -> {
                webView.reload()
            }
        }
    }
    
    /**
     * Android 调用 JavaScript 方法
     */
    private fun callJavaScript() {
        // 方法1：调用无参方法
        webView.evaluateJavascript("showMessage();") { result ->
            Toast.makeText(this, "JS方法返回值: $result", Toast.LENGTH_SHORT).show()
        }
        
        // 方法2：调用有返回值的方法
        webView.postDelayed({
            webView.evaluateJavascript("getDataFromJS();") { result ->
                tvStatus.text = "JS返回数据: $result"
                Toast.makeText(this, "获取到JS数据: $result", Toast.LENGTH_LONG).show()
            }
        }, 1000)
    }
    
    /**
     * Android 接口类，供 JavaScript 调用
     */
    inner class AndroidInterface {
        
        /**
         * JavaScript 调用此方法显示 Toast
         */
        @JavascriptInterface
        fun showToast(message: String) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        
        /**
         * JavaScript 调用此方法显示消息
         */
        @JavascriptInterface
        fun showMessage(message: String) {
            runOnUiThread {
                tvStatus.text = "收到JS消息: $message"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }
        }
        
        /**
         * JavaScript 调用此方法传递数据
         */
        @JavascriptInterface
        fun receiveData(data: String): String {
            runOnUiThread {
                tvStatus.text = "收到JS数据: $data"
                Toast.makeText(this@MainActivity, "收到数据: $data", Toast.LENGTH_SHORT).show()
            }
            return "Android已收到数据: $data"
        }
        
        /**
         * 页面加载完成回调
         */
        @JavascriptInterface
        fun onPageLoaded(message: String) {
            runOnUiThread {
                tvStatus.text = message
            }
        }
    }
    
    /**
     * 处理返回键
     */
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onPause() {
        super.onPause()
        webView.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        webView.onResume()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}
