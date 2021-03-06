package com.example.aop_part2_chapter08

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
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
                    loadingProgressBar.show()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let { updateAddress(it) }
                    refreshLayout.isRefreshing = false
                    loadingProgressBar.hide()
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    loadingProgressBar.progress = view?.progress!!
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    loadingProgressBar.progress = newProgress
                }
            }
            settings.javaScriptEnabled = true
            loadUrl(defaultAddress)
        }
        addressEditText.apply {
            setText(defaultAddress)
            setSelection(addressEditText.length())
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
            //TODO: action button ????????? ??? ???????????? ?????????
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newUrl = v.text.toString()
                if(URLUtil.isNetworkUrl(newUrl)) {
                    mainWebView.loadUrl(newUrl)
                } else {
                    mainWebView.loadUrl("http://$newUrl")
                }
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
    private fun updateAddress(address: String) {
        addressEditText.setText(address)
    }


    fun BackForwardButtonClicked(view: View) {
        when (view.id) {
            R.id.goBackButton -> {
                if (mainWebView.canGoBack()) {
                    //TODO: ????????? ???????????? ????????? ???????????? ?????? ????????? ??????
                    mainWebView.goBack()
                } else {
                    finish()
                }
            }

            R.id.goForwardButton -> {
                if (mainWebView.canGoForward()) {
                    //TODO: ??? ???????????? ????????? ??????????????? ?????? ????????? ??????
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
}