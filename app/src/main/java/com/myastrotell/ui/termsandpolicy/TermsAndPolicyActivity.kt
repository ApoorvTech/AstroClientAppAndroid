package com.myastrotell.ui.termsandpolicy

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityTermsAndPolicyBinding
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_payment_webview.*
import kotlinx.android.synthetic.main.activity_terms_and_policy.*
import kotlinx.android.synthetic.main.activity_terms_and_policy.webView
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class TermsAndPolicyActivity :
    BaseActivity<ActivityTermsAndPolicyBinding, TermsAndPolicyViewModel>() {

    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            type = it.getStringExtra(AppConstants.KEY_DATA) ?: ""
        }

        atvTitle.text = type

        setUpWebViewSettings()

        viewModel?.getTermsAndPolicyData()

    }

    override fun getLayoutId() = R.layout.activity_terms_and_policy

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<TermsAndPolicyViewModel>()

    override fun initVariables() {

    }

    override fun setObservers() {
        viewModel?.termsAndPolicyLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.privacyPolicyData?.let { dataList ->
                dataList.forEach { data ->
                    if (data.policyTitle.equals(type, true)) {
                        webView.loadData(data.policyDescription ?: "", "text/html", "utf-8")
                        return@forEach
                    }

                }
            }

        })

        aivBack.setOnClickListener {
            onBackPressed()
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebViewSettings() {
        webView?.settings!!.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            databaseEnabled = true
            domStorageEnabled = true
            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(true)
            setAppCacheEnabled(true)
        }


        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView?, url: String?, favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                showProgressBar()
            }


            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
//                if (request?.url != null) {
//                    val url = request.url
//
//                }
                return false
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideProgressBar()
            }
        }

    }


    override fun showProgressBar() {
        progressBar?.visible()
    }


    override fun hideProgressBar() {
        progressBar?.gone()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}