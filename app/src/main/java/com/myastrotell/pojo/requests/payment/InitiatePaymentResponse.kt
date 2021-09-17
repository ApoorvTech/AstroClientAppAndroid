package com.myastrotell.pojo.requests.payment

data class InitiatePaymentResponse (val redirectUrl:String, val orderId:String, val appId:String)