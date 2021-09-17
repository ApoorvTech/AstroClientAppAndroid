package com.myastrotell.pojo.response

data class SearchByCategoryResponse(
    val productDetails: List<ProductDetail>
)

data class ProductDetail(
    val campaignId: Int,
    val clientId: String,
    val isAvailable: Int,
    val language: String,
    val productCategory: String,
    val productDescription: String,
    val productId: Int,
    val productImage: String,
    val productImages: List<Any>,
    val productSubCategory: String,
    val productTitle: String,
    val productVideo: String,
    val shortDescription: Any
)