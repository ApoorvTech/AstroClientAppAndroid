package com.myastrotell.pojo.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RedemptionSearchResponse(
    val campaignId: Int,
    val clientId: String,
    val goodsBrief: String,
    val goodsCategory: String,
    val goodsDeliveryTime: String,
    val goodsDescription: String,
    val goodsId: String,
    val goodsImage: String,
    val goodsName: String,
    val goodsPoints: Double,
    val goodsPrice: Double,
    val goodsQuantity: Int,
    val goodsShortDescription: String?,
    val goodsVideo: String?,
    val language: String,
    val redeemFlag: Boolean,
    val sequence: String,
    val updateTimestamp: String,
    val whiteGoodImages: List<String>,
    var isAddedToCart: Boolean = false
) : Parcelable