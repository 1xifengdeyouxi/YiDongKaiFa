package com.swu.webviewweather

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener, TextView.OnEditorActionListener {
    
    private lateinit var webView: WebView
    private lateinit var etUrl: EditText
    private lateinit var btnGo: Button
    private lateinit var btnBack: Button
    private lateinit var btnForward: Button
    private lateinit var btnRefresh: Button
    private lateinit var btnWeather: Button
    private lateinit var tvStatus: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupWebView()
        
        // 默认加载天气网站
        loadWeatherWebsite()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        webView = findViewById(R.id.web_view)
        etUrl = findViewById(R.id.et_url)
        btnGo = findViewById(R.id.btn_go)
        btnBack = findViewById(R.id.btn_back)
        btnForward = findViewById(R.id.btn_forward)
        btnRefresh = findViewById(R.id.btn_refresh)
        btnWeather = findViewById(R.id.btn_weather)
        tvStatus = findViewById(R.id.tv_status)
        
        btnGo.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        btnForward.setOnClickListener(this)
        btnRefresh.setOnClickListener(this)
        btnWeather.setOnClickListener(this)
        
        // 设置输入框回车键监听
        etUrl.setOnEditorActionListener(this)
    }
    
    /**
     * 设置WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webSettings = webView.settings
        
        // 启用JavaScript
        webSettings.javaScriptEnabled = true
        
        // 支持缩放
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        
        // 设置缓存模式
        webSettings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        
        // 支持DOM存储
        webSettings.domStorageEnabled = true
        
        // 支持多窗口
        webSettings.setSupportMultipleWindows(false)
        
        // 允许访问文件
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        
        // 设置UserAgent
        webSettings.userAgentString = webSettings.userAgentString + " WebViewWeatherApp"
        
        // 设置WebViewClient，处理页面加载
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                tvStatus.text = "正在加载: $url"
                updateNavigationButtons()
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                tvStatus.text = "加载完成: $url"
                etUrl.setText(url)
                updateNavigationButtons()
            }
            
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    tvStatus.text = "加载错误: ${error?.description}"
                    Toast.makeText(this@MainActivity, "加载失败: ${error?.description}", Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                // 处理SSL错误（仅用于开发测试，生产环境应正确处理）
                handler?.proceed()
            }
        }
        
        // 设置WebChromeClient，处理进度和标题
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) {
                    tvStatus.text = "加载中: $newProgress%"
                }
            }
            
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                // 可以在这里更新标题栏
            }
        }
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_go -> {
                loadUrl()
            }
            R.id.btn_back -> {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    Toast.makeText(this, "无法后退", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_forward -> {
                if (webView.canGoForward()) {
                    webView.goForward()
                } else {
                    Toast.makeText(this, "无法前进", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_refresh -> {
                webView.reload()
            }
            R.id.btn_weather -> {
                loadWeatherWebsite()
            }
        }
    }
    
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_GO || 
            (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
            loadUrl()
            return true
        }
        return false
    }
    
    /**
     * 加载URL
     */
    private fun loadUrl() {
        var url = etUrl.text.toString().trim()
        
        if (url.isEmpty()) {
            Toast.makeText(this, "请输入网址", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 如果不是以http://或https://开头，添加https://
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            // 检查是否是搜索关键词（包含中文或空格）
            if (url.contains(" ") || url.any { it in '\u4e00'..'\u9fff' }) {
                // 使用百度搜索
                url = "https://www.baidu.com/s?wd=$url"
            } else {
                // 添加https://
                url = "https://$url"
            }
        }
        
        webView.loadUrl(url)
    }
    
    /**
     * 加载天气网站
     */
    private fun loadWeatherWebsite() {
        // 加载中国天气网
        val weatherUrl = "https://weather.com.cn/"
        etUrl.setText(weatherUrl)
        webView.loadUrl(weatherUrl)
    }
    
    /**
     * 更新导航按钮状态
     */
    private fun updateNavigationButtons() {
        btnBack.isEnabled = webView.canGoBack()
        btnForward.isEnabled = webView.canGoForward()
    }
    
    /**
     * 处理返回键
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
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
