package com.myastrotell.pojo.requests

data class RedemptionSearchRequest(
    val redeemCategory: String?,
    val redemptionType: String? = null
)