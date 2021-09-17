package com.myastrotell.pojo.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ProductBillingDetails(
    val availableCount: Int,
    val campaignId: String?,
    val clientId: String?,
    val id: String,
    var isBusy: Boolean = false,
    var isOnline: Boolean = false,
    val imageURL: String?,
    val productName: String?,
    val redeemCategory: String?,
    val redeemDate: Long,
    val redeemFlag: Boolean,
    val redeemMode: String?,
    val redeemPoint: Double,
    val redeemUnit: String?,
    val redeemValue: Double
) : Parcelable