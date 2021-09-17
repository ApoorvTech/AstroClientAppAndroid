package com.myastrotell.pojo.requests.payment

import com.myastrotell.BuildConfig
import com.myastrotell.data.DataManager

data class InitiatePaymentModel(
    var amount: String,
    var amountWithGst: String,
    val voucher_transaction_id: String? = "",
    val billingAddress: String? = "",
    val billingCity: String? = "",
    val billingCountry: String? = "",
    val billingName: String? = "",
    val billingState: String? = "",
    val billingTel: String = DataManager.getMsisdn(),
    val billingZip: String? = "",
    val appId: Int = 1,
    val channel: String = BuildConfig.CHANNEL,
)