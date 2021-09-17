package com.myastrotell.pojo.requests.buymallproduct

data class BuyProductRequest(
    val products: List<ProductInfo>,
    val redemptionAddress: String? = null,
    val address: ShippingAddress,
    val totalRedeemPoints: Double,
    val paytmNumber: String? = null,
    val isSelfPaytm: String? = null,
    val isQuantityCheck: String? = null,
    val voucher_transaction_id: String? = null
)