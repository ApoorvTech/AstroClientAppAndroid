package com.myastrotell.pojo.requests

data class WalletPassbookRequest(
    var searchKey: String = "",
    var startDate: String = "0",
    var endDate: String = "0",
    var pageNumber: Int = 0,
    var entryList: String = ""
)