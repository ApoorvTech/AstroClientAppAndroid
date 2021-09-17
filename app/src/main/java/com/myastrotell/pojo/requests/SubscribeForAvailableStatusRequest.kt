package com.myastrotell.pojo.requests

data class SubscribeForAvailableStatusRequest(
    val productType: String? = "",
    val productId: String?
)