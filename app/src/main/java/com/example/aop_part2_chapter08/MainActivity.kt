package com.example.aop_part2_chapter08

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
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

    private val refreshLayout : SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    private lateinit var addressList: ArrayList<String>
    private var currentPosition : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSettings()
        intentWebBrowser()
        refreshLayout()

    }

    private fun initSettings() {
        mainWebView.apply {
            webViewClient = WebViewClientClass()
            loadUrl(defaultAddress)
        }
        addressEditText.apply {
            setText(defaultAddress)
            setSelection(addressEditText.length())
        }

        addressList = ArrayList<String>()
        addressList.add(currentPosition, defaultAddress)
    }

    private fun intentWebBrowser() {
        addressEditText.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                val newURL = addressEditText.text.toString()
                mainWebView.loadUrl(newURL)
                addressList.add(++currentPosition, newURL)
                true
            } else
                false
        }

    }

    private fun refreshLayout() {
        refreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                mainWebView.reload()
            }
        })
    }


    fun BackForwardButtonClicked(view: View) {
        when (view.id) {
            R.id.goBackButton -> {
                if (mainWebView.canGoBack()) {
                    //TODO: 뒤로갈 페이지가 있으면 뒤로가기 버튼 클릭시 이동
                    mainWebView.goBack()
                    addressEditText.setText(addressList[--currentPosition])
                } else {
                    finish()
                }
            }

            R.id.goForwardButton -> {
                if (mainWebView.canGoForward()) {
                    //TODO: 앞 페이지가 있으면 앞으로가기 버튼 클릭시 이동
                    mainWebView.goForward()
                    addressEditText.setText(addressList[++currentPosition])
                }
            }
        }
    }

    fun homeButtonClicked(view: View) {
        when (view.id) {
            R.id.homeButton -> {
                if(mainWebView.canGoBack()){
                    mainWebView.loadUrl(defaultAddress)
                    addressList.add(++currentPosition, defaultAddress)
                    addressEditText.setText(addressList[0])
                }
            }
        }
    }

    override fun onBackPressed() {
        if(mainWebView.canGoBack()){
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