package com.myastrotell.pojo.response

data class CallRequestResponse(
    val campaignId: Int,
    val clientId: String,
    val dialStatus: Int,
    val language: String,
    val no2dial: Long,
    val ratePerMinute: Double,
    val txnId: String,
    val updateTimestamp: String,
    val userBalance: Double,
    val userNumber: Long
)