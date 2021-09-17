package com.myastrotell.ui.payments.paymentgateway

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.pojo.requests.payment.InitiatePaymentModel
import com.myastrotell.pojo.requests.payment.InitiatePaymentResponse
import kotlinx.coroutines.launch


class PaymentViewModel : BaseViewModel() {

    lateinit var amount: String
    lateinit var totalAmount: String
    lateinit var webViewUrl: String

    var voucherTransactionId: String? = null

    val redirectUrl = BuildConfig.PAYMENT_API_BASE_URL + BuildConfig.PAYMENT_REDIRECT_URL
    val cancelUrl = BuildConfig.PAYMENT_API_BASE_URL + BuildConfig.PAYMENT_CANCEL_URL


    val initPaymentLiveData by lazy { MutableLiveData<InitiatePaymentResponse>() }
    val paymentUrlLiveData by lazy { MutableLiveData<String>() }
    val rechargeDetailsLiveData by lazy { MutableLiveData<Boolean>() }


    fun setIntent(intent: Intent?) {
        intent?.let {
            amount = it.getStringExtra(AppConstants.KEY_AMOUNT) ?: "0"
            totalAmount = it.getStringExtra(AppConstants.KEY_TOTAL_AMOUNT) ?: "0"
            voucherTransactionId = it.getStringExtra(AppConstants.KEY_VOUCHER_TRANSACTION_ID) ?: ""

            initiatePayment()

        }
    }


     fun getRechargeDetails() {
        if (navigator?.isNetworkAvailable() == true) {
            viewModelScope.launch {
                PaymentRepo.getRechargeDetails(
                    getPhone(),
                    ApiRequestCodes.RECHARGE,
                    rechargeDetailsLiveData,
                    errorLiveData
                )

            }
        }

    }







    /**
     * initiate payment with requested amount
     */
    private fun initiatePayment() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            val model = InitiatePaymentModel(
                amount = amount,
                amountWithGst = totalAmount,
                voucher_transaction_id = voucherTransactionId
            )

            viewModelScope.launch {
                PaymentRepo.initiatePayment(
                    model,
                    ApiRequestCodes.INITIATE_PAYMENT,
                    initPaymentLiveData,
                    errorLiveData
                )

            }
        } else {
            navigator?.showNoNetworkError()
        }

    }


    fun handleResponse(response: InitiatePaymentResponse) {
        response.let {
            webViewUrl = (BuildConfig.PAYMENT_API_BASE_URL + it.redirectUrl + it.orderId)
            paymentUrlLiveData.value = webViewUrl
        }

    }

}