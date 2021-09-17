package com.myastrotell.pojo.requests

data class AuthenticateVoucherRequest(
    var promocode: String?,
    var productName: String?,
    var productCost: String?
)