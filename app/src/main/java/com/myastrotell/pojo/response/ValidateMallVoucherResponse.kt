package com.myastrotell.pojo.response

data class ValidateMallVoucherResponse(
    var discountedTotalPoints: Double?,
    var min_amount: Double?,
    var totalDiscount: Double?,
    var voucher_transaction_id: String?,
    var promoCode: String?
)