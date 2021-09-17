package com.myastrotell.pojo.requests.buymallproduct

import com.myastrotell.data.ProductType

data class ProductInfo(
    val redeemParentCategory: String = ProductType.WHITE_GOODS.value,
    val redeemCategory: String,
    val redeemMode: String,
    val redeemPoint: String,  //total
    val redeemValue: String,  //1 unit price
    val redeemUnit: String,
    val redeemId: String
)