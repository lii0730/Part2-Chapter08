package com.example.aop_part2_chapter08

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    companion object {
        private const val defaultAddress: String = "https://www.google.com"
    }

    private val mainWebView: WebView by lazy {
        findViewById(R.id.mainWebView)
    }

    private val addressEditText: EditText by lazy {
        findViewById(R.id.addressEditText)
    }

    private val loadingProgressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.loadingProgressBar)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSettings()
        intentWebBrowser()
        refreshLayout()

    }

    private fun initSettings() {
        mainWebView.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.let { it.loadUrl(url!!) }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    loadingProgressBar.visibility = View.VISIBLE
                    loadingProgressBar.progress = view?.progress!!
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let { updateAddress(it) }
                    refreshLayout.isRefreshing = false
                    loadingProgressBar.progress = view?.progress!!
                    loadingProgressBar.visibility = View.INVISIBLE
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    loadingProgressBar.progress = view?.progress!!
                }
            }
            settings.javaScriptEnabled = true
            loadUrl(defaultAddress)
        }
        addressEditText.apply {
            setText(defaultAddress)
            setSelection(addressEditText.length())
            setOnClickListener {
                selectAll()
            }
        }
    }

    private fun intentWebBrowser() {
        addressEditText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val newURL = addressEditText.text.toString()
                mainWebView.loadUrl(newURL)
                true
            } else
                false
        }

        addressEditText.setOnEditorActionListener { v, actionId, event ->
            //TODO: action button 눌렀을 떄 발생하는 이벤트
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mainWebView.loadUrl(v.text.toString())
            }
            return@setOnEditorActionListener false
        }
    }

    private fun refreshLayout() {
        refreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mainWebView.reload()
            }
        })
    }

    private fun webLoadCompleted() {

    }

    private fun updateAddress(address: String) {
        addressEditText.setText(address)
    }


    fun BackForwardButtonClicked(view: View) {
        when (view.id) {
            R.id.goBackButton -> {
                if (mainWebView.canGoBack()) {
                    //TODO: 뒤로갈 페이지가 있으면 뒤로가기 버튼 클릭시 이동
                    mainWebView.goBack()
                } else {
                    finish()
                }
            }

            R.id.goForwardButton -> {
                if (mainWebView.canGoForward()) {
                    //TODO: 앞 페이지가 있으면 앞으로가기 버튼 클릭시 이동
                    mainWebView.goForward()
                }
            }
        }
    }

    fun homeButtonClicked(view: View) {
        when (view.id) {
            R.id.homeButton -> {
                mainWebView.loadUrl(defaultAddress)
            }
        }
    }

    override fun onBackPressed() {
        if (mainWebView.canGoBack()) {
            mainWebView.goBack()
        } else {
            finish()
        }
    }

    inner class WebViewClientClass : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url!!)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loadingProgressBar.visibility = ProgressBar.VISIBLE
            mainWebView.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            loadingProgressBar.visibility = ProgressBar.GONE
            mainWebView.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            addressEditText.setText(url)
            refreshLayout.isRefreshing = false
        }
    }
}