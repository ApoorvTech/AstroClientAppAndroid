package com.myastrotell.pojo.requests



data class SubscribeForOnlineStatusRequest(
    val productType: String? = "",
    val productId: String?,
    val notificationTitle: String = "",
    val notificationText: String = ""
)