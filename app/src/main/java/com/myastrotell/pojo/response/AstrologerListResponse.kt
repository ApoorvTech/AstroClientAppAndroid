package com.myastrotell.pojo.response

import java.util.Comparator

data class AstrologerListResponse(
    var campaignId: Int?,
    var clientId: String?,
    var goodsBrief: String?,
    var goodsCategory: String?,
    var goodsDeliveryTime: String?,
    var goodsDescription: String?,
    var goodsId: String?,
    var goodsImage: String?,
    var goodsName: String?,
    var goodsCode: String?,
    var goodsPoints: Double?,
    var goodsPrice: Double?,
    var goodsQuantity: Int?,
    var goodsSale: Int?,
    var isBusy: Boolean = false,
    var goodsShortDescription: String?,
    var goodsVideo: String?,
    var goodsLanguage: String?,
    var redeemFlag: Boolean?,
    var sequence: String?,
    var goodsAttribute: String?,
    var goodsTotalRating: Long? = 0,
    var goodsAvgRating: Float? = 0f,
    var updateTimestamp: String?,
    var whiteGoodImages: List<Any>?
):Comparator<AstrologerListResponse>{
    override fun compare(obj1: AstrologerListResponse?, obj2: AstrologerListResponse?): Int {
        if((obj1?.sequence?.toInt()!!>0 && obj2?.sequence?.toInt()!!>0) &&(obj1?.sequence?.toInt()!! >obj2?.sequence?.toInt()!!))
            return  1
        else if(obj1?.sequence?.toInt()!! ==obj2?.sequence?.toInt()!!)
            return 0
        else
            return -1
    }
}
