package com.myastrotell.pojo.response

data class OfferListResponse(
    var active: Boolean?,
    var amtAfterDiscount: Double?,
    var campaignId: Int?,
    var cardAmount: Double?,
    var channel: String?,
    var clientId: String?,
    var discountAmount: Double?,
    var discountPercent: Double?,
    var endDate: String?,
    var finalAmtWithGst: Double?,
    var gstAmount: Double?,
    var gstPercent: Double?,
    var id: Int?,
    var startDate: String?,
    var isSelected: Boolean = false
)