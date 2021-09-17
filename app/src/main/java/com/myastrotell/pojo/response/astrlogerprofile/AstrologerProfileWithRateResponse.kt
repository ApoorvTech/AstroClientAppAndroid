package com.myastrotell.pojo.response.astrlogerprofile

data class AstrologerProfileWithRateResponse(
    var campaignId: Int?,
    var clientId: String?,
    var goodsBrief: String?,
    var goodsCategory: String?,
    var goodsDeliveryTime: String?,
    var goodsDescription: String?,
    var goodsId: String?,
    var goodsImage: String?,
    var goodsName: String?,
    var goodsPoints: Double?,
    var goodsPrice: Double?,
    var goodsQuantity: Int?,
    var goodsShortDescription: String?,
    var goodsVideo: String?,
    var language: String?,
    var redeemFlag: Boolean?,
    var sequence: String?,
    var updateTimestamp: String?,
    var whiteGoodImages: List<Any>?
)