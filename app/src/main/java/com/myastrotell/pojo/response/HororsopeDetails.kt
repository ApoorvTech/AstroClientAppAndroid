package com.myastrotell.pojo.response

data class HororsopeResponse(
    val productDetails : List<HororsopeDetails>?
)


data class HororsopeDetails(
    var campaignId: Int?,
    var clientId: String?,
    var isAvailable: Int?,
    var language: String?,
    var productCategory: String?,
    var productDescription: String?,
    var productId: Int?,
    var productImage: String?,
    var productSubCategory: String?,
    var productSubCategoryImage: String?,
    var productTitle: String?,
    var updateTimestamp: String?
)