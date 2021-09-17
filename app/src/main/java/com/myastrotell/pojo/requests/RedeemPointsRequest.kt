package com.myastrotell.pojo.requests

data class RedeemPointsRequest(
    val address: Address,
    val appLanguage: String,
    val isQuantityCheck: String,
    val isSelfPaytm: String,
    val paytmNumber: String,
    val redeemCategory: String,
    val redeemMode: String,
    val redeemParentCategory: String,
    val redeemPoint: String,
    val redeemUnit: String,
    val redeemValue: String,
    val redemptionAddress: String
)

data class Address(
    val deliveryMobileNo: String,
    val deliveryName: String
)