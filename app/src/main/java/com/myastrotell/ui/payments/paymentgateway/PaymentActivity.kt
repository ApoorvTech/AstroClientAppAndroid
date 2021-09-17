package com.myastrotell.ui.payments.paymentgateway

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import com.myastrotell.BaseApplication
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.CapturedEvents
import com.myastrotell.databinding.ActivityPaymentWebviewBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_payment_webview.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class PaymentActivity : BaseActivity<ActivityPaymentWebviewBinding, PaymentViewModel>() {


    private var allowToGoBack: Boolean = true
    private var isFirstRecharge: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpWebViewSettings()

        viewModel?.setIntent(intent)

        viewModel?.getRechargeDetails()

    }


    override fun getLayoutId() = R.layout.activity_payment_webview


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<PaymentViewModel>()


    override fun initVariables() {
    }


    override fun setObservers() {

        viewModel?.rechargeDetailsLiveData?.observe(this, Observer {
            it?.let {
                isFirstRecharge = !it
            }
        })

        //initiate payment & get url to be loaded
        viewModel?.initPaymentLiveData?.observe(this, Observer {
            hideProgressBar()
            it?.let {
                viewModel?.handleResponse(it)
            }
        })


        //loading payment url in webView
        viewModel?.paymentUrlLiveData?.observe(this, Observer {
            webView.loadUrl(it)
        })


        aivBack?.setOnClickListener {
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
                if (request?.url != null) {
                    val url = request.url
                    if (url.toString().contains(viewModel?.redirectUrl.toString(), true)) {
                        hideProgressBar()

                        val uri = Uri.parse(url.toString())
                        val orderStatus = uri.getQueryParameter("order_status")

                        if (orderStatus.equals("success", true)) {
                            // when transaction is successful
                            if (isFirstRecharge)
                                BaseApplication.instance.capturePurchaseEvent(
                                    CapturedEvents.TRANSACTION_STATUS.SUCCESS,
                                    viewModel?.amount!!,
                                    viewModel?.getPhone()!!
                                )


                            BaseApplication.instance.captureRechargeEvent(
                                CapturedEvents.TRANSACTION_STATUS.SUCCESS,
                                viewModel?.amount!!,
                                viewModel?.getPhone()!!
                            )


                            AppAlertDialog(
                                this@PaymentActivity,
                                R.drawable.ic_alert,
                                getString(R.string.payment_successful),
                                "Your transaction of ₹${viewModel?.amount} is successfully added to your wallet.",
                                getString(R.string.ok),
                                null,
                                onPositiveBtnClicked = {
                                    val intent = Intent()
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                            ).show()

                        } else {

//                            if (isFirstRecharge)
//                                BaseApplication.instance.capturePurchaseEvent(
//                                    CapturedEvents.TRANSACTION_STATUS.FAIL,
//                                    viewModel?.amount!!,
//                                    viewModel?.getPhone()!!
//                                )
//
//                            BaseApplication.instance.captureRechargeEvent(
//                                CapturedEvents.TRANSACTION_STATUS.FAIL,
//                                viewModel?.amount!!,
//                                viewModel?.getPhone()!!
//                            )

                            AppAlertDialog(
                                this@PaymentActivity,
                                R.drawable.ic_alert,
                                getString(R.string.payment_failed),
                                "Your transaction of ₹${viewModel?.amount} has been failed. Please try after some time.",
                                getString(R.string.ok),
                                null,
                                onPositiveBtnClicked = {
                                    setResult(0, null)
                                    finish()
                                }
                            ).show()
                        }

                        return true

                    } else if (url.toString().contains(viewModel?.cancelUrl.toString(), true)) {
                        // transaction cancelled by user
                        hideProgressBar()

//                        if (isFirstRecharge)
//                            BaseApplication.instance.capturePurchaseEvent(
//                                CapturedEvents.TRANSACTION_STATUS.CANCEL_BY_USER,
//                                viewModel?.amount!!,
//                                viewModel?.getPhone()!!
//                            )
//
//
//                        BaseApplication.instance.captureRechargeEvent(
//                            CapturedEvents.TRANSACTION_STATUS.CANCEL_BY_USER,
//                            viewModel?.amount!!,
//                            viewModel?.getPhone()!!
//                        )


                        AppAlertDialog(
                            this@PaymentActivity,
                            R.drawable.ic_alert,
                            getString(R.string.payment_cancelled),
                            "Your transaction of ₹${viewModel?.amount} has been cancelled.",
                            getString(R.string.ok),
                            null,
                            onPositiveBtnClicked = {
                                setResult(0, null)
                                finish()
                            }
                        ).show()
                        return true
                    }
                }
                return false
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideProgressBar()
            }
        }

    }


    override fun onBackPressed() {
        if (allowToGoBack) {
            AppAlertDialog(
                this,
                R.drawable.ic_alert,
                getString(R.string.cancel_payment),
                getString(R.string.sure_to_cancel_payment),
                getString(R.string.confirm),
                getString(R.string.cancel),
                onPositiveBtnClicked = {
                    setResult(0, null)
                    finish()
                }
            ).show()
        }
    }


}