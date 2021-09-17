package com.myastrotell.pojo.requests.buymallproduct

data class ValidateMallVoucherRequest(
    var products: List<ProductInfo>?,
    var productName: String?,
    var promocode: String?,
    var totalRedeemPoints: String?
)