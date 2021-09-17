package com.myastrotell.pojo.response

data class WalletBalanceResponse(
    var endDate: Long?,
    var myPoint: MyPoint?,
    var myPointTitle: List<MyPointTitle>?,
    var startDate: Long?
)