package com.myastrotell.ui.payments

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.webkit.*
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import com.myastrotell.BR
import com.myastrotell.BaseApplication
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.ApiEndPoints
import com.myastrotell.data.AppConstants
import com.myastrotell.data.AvenueParams
import com.myastrotell.databinding.ActivityWebViewBinding
import com.myastrotell.ui.payments.dialogs.ActionDialog
import com.myastrotell.ui.payments.fragments.ApproveOTPFragment
import com.myastrotell.ui.payments.fragments.CityBankFragment
import com.myastrotell.ui.payments.fragments.PaymentOtpFragment
import com.myastrotell.ui.payments.interfaces.Communicator
import com.myastrotell.ui.payments.status.StatusActivity
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_web_view.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*


class WebViewActivity :BaseActivity<ActivityWebViewBinding,WebViewActivityViewModel>(),
    Communicator {
    override fun getLayoutId()= R.layout.activity_web_view
    override fun getBindingVariable()=BR._all
    private var bankUrl:String=""
    private var MyDeviceAPI:Int=0
    private var loadCounter:Int=0
    private  var mIntentReceiver:BroadcastReceiver?=null
    private  var actionDialog:ActionDialog?=null
    private var timerTask:TimerTask?=null
    private var timer:Timer?=null
    private val handler=Handler()

    override fun initViewModel()=getViewModel<WebViewActivityViewModel>()

    override fun initVariables() {
    }

    override fun setObservers() {
        viewModel?.getKeyLiveData()?.observe(this, Observer {
            hideProgressBar()
            it?.let {
                viewModel?.handleKey(it)
                showLongToast(it)
            }


        })
        viewModel?.getEncryptedValue()?.observe(this, Observer {
            hideProgressBar()
            it?.let {
                setupWebViewWithData(it)
            }


        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel?.setIntent(intent)
        viewModel?.getRsaKey()

    }

    private fun setupWebViewWithData(data:String) {
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        webView.webViewClient = object : WebViewClient() {

           override fun  shouldOverrideUrlLoading( view:WebView, url:String) :Boolean{
                bankUrl = url;
                return false;
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                bankUrl= request?.url.toString()
                return false
            }
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
               hideProgressBar()
                if (url.indexOf("/ccavResponseHandler.jsp") != -1) {
                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                }


                // calling load Waiting for otp fragment
                if (loadCounter < 1) {
                    if (MyDeviceAPI >= 19) {
                        loadCitiBankAuthenticateOption(url);
                        loadWaitingFragment(url);

                    }
                }
                bankUrl = url;
            }

            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap
            ) {
                super.onPageStarted(view, url, favicon)
               showProgressBar()
            }
        }


        try {
            val postData: String =
                AvenueParams.ACCESS_CODE.toString() + "=" + URLEncoder.encode(
                    viewModel?.accessCode, "UTF-8"
                ) + "&" + AvenueParams.MERCHANT_ID + "=" + URLEncoder.encode(
                    viewModel?.merchantId, "UTF-8"
                ) + "&" + AvenueParams.ORDER_ID + "=" + URLEncoder.encode(
                    viewModel?.orderIdForPayment, "UTF-8"
                ) + "&" + AvenueParams.REDIRECT_URL + "=" + URLEncoder.encode(
                    viewModel?.redirectUrl, "UTF-8"
                ) + "&" + AvenueParams.CANCEL_URL + "=" + URLEncoder.encode(
                    viewModel?.cancelUrl, "UTF-8"
                ) + "&" + AvenueParams.ENC_VAL + "=" + URLEncoder.encode(data, "UTF-8")
            webView.postUrl(ApiEndPoints.TRANS_URL, postData.toByteArray())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }
    private  fun startTimer(){
        try {
            //set a new Timer
            if (timer == null) {
                timer =  Timer();
            }
            //initialize the TimerTask's job
            initializeTimerTask();

            //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
            timer?.schedule(timerTask, 30000, 30000);
        } catch ( e:Exception) {
            e.printStackTrace();
        }

    }

    private fun loadWaitingFragment(url: String) {
        // SBI Debit Card
        if (url.contains("https://acs.onlinesbi.com/sbi/")) {
            val  waitingFragment =  PaymentOtpFragment()
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer()
        }

        // Kotak Bank Visa Debit card
        else if (url.contains("https://cardsecurity.enstage.com/ACSWeb/")) {
            val  waitingFragment =  PaymentOtpFragment();
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // For SBI and All its Asscocites Net Banking
        else if (url.contains("https://merchant.onlinesbi.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbi.com/merchant/resendsmsotp.htm") || url.contains("https://m.onlinesbi.com/mmerchant/smsenablehighsecurity.htm")
            || url.contains("https://merchant.onlinesbh.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbh.com/merchant/resendsmsotp.htm")
            || url.contains("https://merchant.sbbjonline.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.sbbjonline.com/merchant/resendsmsotp.htm")
            || url.contains("https://merchant.onlinesbm.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbm.com/merchant/resendsmsotp.htm")
            || url.contains("https://merchant.onlinesbp.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbp.com/merchant/resendsmsotp.htm")
            || url.contains("https://merchant.sbtonline.in/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.sbtonline.in/merchant/resendsmsotp.htm")) {
            val  waitingFragment =  PaymentOtpFragment();
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }

        // For ICICI Credit Card
        else if (url.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer")) {
            val  waitingFragment =  PaymentOtpFragment();
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // City bank Debit card
        else if (url.equals("cityBankAuthPage")) {
            removeCitiBankAuthOption();
            val  waitingFragment =  PaymentOtpFragment();
            val transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // HDFC Debit Card and Credit Card
        else if (url.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth")) {
            //removeCitiBankAuthOption();
            val  waitingFragment =  PaymentOtpFragment();
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // For SBI  Visa credit Card
        else if (url.contains("https://secure4.arcot.com/acspage/cap")) {
            val  waitingFragment =  PaymentOtpFragment();
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }

        // For Kotak Bank Visa Credit Card
        else if (url.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer")) {
            val  waitingFragment =  PaymentOtpFragment();
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        } else {
            removeWaitingFragment();
            removeApprovalFragment();
            stopTimerTask();
        }
    }
    private  fun removeCitiBankAuthOption(){
        val fragment=supportFragmentManager.findFragmentByTag("CitiBankAuthFrag")
        fragment?.let {
            val transaction=supportFragmentManager.beginTransaction()
            transaction.remove(it)
            transaction.commit()

        }
    }

    private fun loadCitiBankAuthenticateOption(url: String) {
        if (url.contains("https://www.citibank.co.in/acspage/cap_nsapi.so")) {
            val  citiFrag =  CityBankFragment()
             val transaction = supportFragmentManager.beginTransaction();
             transaction.add(R.id.otp_frame, citiFrag, "CitiBankAuthFrag");
            transaction.commit();
            loadCounter++;
        }

    }

    internal class MyJavaScriptInterface {
        @JavascriptInterface
        fun processHTML(html: String) {
            // process the html source code to get final status of transaction
            var status: String? = null
            status = if (html.indexOf("Failure") != -1) {
                "Transaction Declined!"
            } else if (html.indexOf("Success") != -1) {
                "Transaction Successful!"
            } else if (html.indexOf("Aborted") != -1) {
                "Transaction Cancelled!"
            } else {
                "Status Not Known!"
            }
            //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
          callActivity(status)
        }

        private fun callActivity(status: String) {
            val intent=Intent(BaseApplication.instance.applicationContext, StatusActivity::class.java)
            intent.putExtra(AppConstants.KEY_DATA, status)
            startActivity(BaseApplication.instance.applicationContext,intent,null)

        }
    }

    override fun onPause() {
        super.onPause()
        if(mIntentReceiver!=null)
            this.unregisterReceiver(this.mIntentReceiver)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("SmsMessage.intent.MAIN")
        mIntentReceiver = object : BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            override fun onReceive(context: Context?, intent: Intent) {
                try {
                    //removeWaitingFragment();
                    removeApprovalFragment()
                    ///////////////////////////////////////
                    val msgText = intent.getStringExtra("get_otp")
                    val otp = msgText!!.split("\\|".toRegex()).toTypedArray()[0]
                    val senderNo =
                        msgText.split("\\|".toRegex()).toTypedArray()[1]
                    if (MyDeviceAPI >= 19) {
                        loadApproveOTP(otp, senderNo)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Exception :$e", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        this.registerReceiver(mIntentReceiver, intentFilter)
    }

    fun removeApprovalFragment() {
        val approveOTPFragment =
            supportFragmentManager.findFragmentByTag("OTPApproveFrag") as ApproveOTPFragment
        if (approveOTPFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(approveOTPFragment)
            transaction.commit()
        }
    }

    fun loadActionDialog() {
        try {
            actionDialog?.show(supportFragmentManager, "ActionDialog")
            stopTimerTask()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // Method to Initialize Task
    fun initializeTimerTask() {
        try {
            timerTask = object : TimerTask() {
                override fun run() {

                    //use a handler to run a toast that shows the current timestamp
                    handler.post(Runnable { /*int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(getApplicationContext(), "I M Called ..", duration);
                                        toast.show();*/
                        loadActionDialog()
                    })
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // Method to stop timer
    fun stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    // Method to load Approve Otp Fragment
    fun loadApproveOTP(otpText: String, senderNo: String) {
        try {
            val vTemp = otpText.toInt()
            if (bankUrl.contains("https://acs.onlinesbi.com/sbi/") && senderNo.contains("SBI") && (otpText.length == 6 || otpText.length == 8)) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/") && senderNo.contains(
                    "KOTAK"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (((bankUrl.contains("https://merchant.onlinesbi.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbi.com/merchant/resendsmsotp.htm"
                ) || bankUrl.contains("https://m.onlinesbi.com/mmerchant/smsenablehighsecurity.htm")) && senderNo.contains(
                    "SBI"
                )
                        || (bankUrl.contains("https://merchant.onlinesbh.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbh.com/merchant/resendsmsotp.htm"
                )) && senderNo.contains("SBH")
                        || (bankUrl.contains("https://merchant.sbbjonline.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.sbbjonline.com/merchant/resendsmsotp.htm"
                )) && senderNo.contains("SBBJ")
                        || (bankUrl.contains("https://merchant.onlinesbm.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbm.com/merchant/resendsmsotp.htm"
                )) && senderNo.contains("SBM")
                        || (bankUrl.contains("https://merchant.onlinesbp.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbp.com/merchant/resendsmsotp.htm"
                )) && senderNo.contains("SBP")
                        || (bankUrl.contains("https://merchant.sbtonline.in/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.sbtonline.in/merchant/resendsmsotp.htm"
                )) && senderNo.contains("SBT")) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer") && senderNo.contains(
                    "ICICI"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://acs.icicibank.com/acspage/cap?") && senderNo.contains(
                    "ICICI"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://www.citibank.co.in/acspage/cap_nsapi.so") && senderNo.contains(
                    "CITI"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth") && senderNo.contains(
                    "HDFC"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://netbanking.hdfcbank.com/netbanking/entry") && senderNo.contains(
                    "HDFC"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://secure4.arcot.com/acspage/cap") && senderNo.contains(
                    "SBI"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer") && senderNo.contains(
                    "KOTAK"
                ) && (otpText.length == 6 || otpText.length == 8)
            ) {
                removeWaitingFragment()
                stopTimerTask()
                val approveFragment = ApproveOTPFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag")
                transaction.commit()
                approveFragment.setOtpText(otpText)
            } else {
                removeApprovalFragment()
                stopTimerTask()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun removeWaitingFragment(){
        val  waitingFragment = supportFragmentManager .findFragmentByTag("OTPWaitingFrag")
        if (waitingFragment != null) {
            val  transaction = supportFragmentManager.beginTransaction();
            transaction.remove(waitingFragment);
            transaction.commit();
        }

    }

    override fun respond(otpText: String) {

        try {
            // For SBI and all the associates
            if (bankUrl.contains("https://acs.onlinesbi.com/sbi/")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('otp').value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('txtOtp').value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://secure4.arcot.com/acspage/cap")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementsByName('pin1')[0].value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://merchant.onlinesbi.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbi.com/merchant/resendsmsotp.htm"
                ) || bankUrl.contains("https://m.onlinesbi.com/mmerchant/smsenablehighsecurity.htm")
                || bankUrl.contains("https://merchant.onlinesbh.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbh.com/merchant/resendsmsotp.htm"
                )
                || bankUrl.contains("https://merchant.sbbjonline.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.sbbjonline.com/merchant/resendsmsotp.htm"
                )
                || bankUrl.contains("https://merchant.onlinesbm.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbm.com/merchant/resendsmsotp.htm"
                )
                || bankUrl.contains("https://merchant.onlinesbp.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.onlinesbp.com/merchant/resendsmsotp.htm"
                )
                || bankUrl.contains("https://merchant.sbtonline.in/merchant/smsenablehighsecurity.htm") || bankUrl.contains(
                    "https://merchant.sbtonline.in/merchant/resendsmsotp.htm"
                )
            ) {
                webView.evaluateJavascript(
                    "javascript:document.getElementsByName('securityPassword')[0].value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('txtAutoOtp').value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://acs.icicibank.com/acspage/cap?")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('txtAutoOtp').value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://www.citibank.co.in/acspage/cap_nsapi.so")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementsByName('otp')[0].value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('txtOtpPassword').value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://netbanking.hdfcbank.com/netbanking/entry")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementsByName('fldOtpToken')[0].value = '$otpText'",
                    ValueCallback<String?> { })
            } else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('otpValue').value = '$otpText'",
                    ValueCallback<String?> { })
            }
            // for CITI Bank Authenticate with option selection
            if (otpText.equals("password")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('uid_tb_r').click();",
                    ValueCallback<String?> { })
            }
            if (otpText.equals("smsOtp")) {
                webView.evaluateJavascript(
                    "javascript:document.getElementById('otp_tb_r').click();",
                    ValueCallback<String?> { })
                loadWaitingFragment("cityBankAuthPage")
            }
            loadCounter++
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }




    }

    override fun actionSelected(data: String) {
        try {
            if (data == "ResendOTP") {
                stopTimerTask()
                removeWaitingFragment()
                if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank")) {
                    webView.evaluateJavascript(
                        "javascript:reSendOtp();",
                        ValueCallback<String?> { })
                } else if (bankUrl.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth")) {
                    webView.evaluateJavascript(
                        "javascript:generateOTP();",
                        ValueCallback<String?> { })
                } else if (bankUrl.contains("https://secure4.arcot.com/acspage/cap")) {
                    webView.evaluateJavascript(
                        "javascript:OnSubmitHandlerResend();",
                        ValueCallback<String?> { })
                } else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer")) {
                    webView.evaluateJavascript(
                        "javascript:doSendOTP();",
                        ValueCallback<String?> { })
                } else if (bankUrl.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer")) {
                    webView.evaluateJavascript(
                        "javascript:resend_otp();",
                        ValueCallback<String?> { })
                } else {
                    webView.evaluateJavascript(
                        "javascript:resendOTP();",
                        ValueCallback<String?> { })
                }
                //loadCounter=0;
            } else if (data == "EnterOTPManually") {
                stopTimerTask()
                removeWaitingFragment()
            } else if (data == "Cancel") {
                stopTimerTask()
                removeWaitingFragment()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(
                applicationContext,
                "Action not available for this Payment Option !",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

}