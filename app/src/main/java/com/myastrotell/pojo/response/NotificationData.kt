package com.myastrotell.pojo.response

data class NotificationData(
    val response: List<Notifications>?
)

data class Notifications(
    val campaignId: Int?,
    val deviceId: String?,
    val id: Int?,
    val imageName: String?,
    val imagePath: String?,
    val isSeen: Int?,
    val isadhoc: Boolean?,
    val message: String?,
    val messageId: Int?,
    val msisdn: String?,
    val requestId: Any?,
    val scheduleDate: Long?,
    val tableId: Int?,
    val targetActionData: String?,
    val targetActionType: String?,
    val title: String?
)