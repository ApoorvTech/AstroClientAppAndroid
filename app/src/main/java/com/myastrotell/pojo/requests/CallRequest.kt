package com.myastrotell.pojo.requests

data class CallRequest(
    val no2dial: String,
    val ratePerMinute: String,
    val userBalance: String,
    val userNumber: String
)