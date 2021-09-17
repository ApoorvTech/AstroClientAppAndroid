package com.myastrotell.pojo.requests

data class ProductBillingRequest(
    var endDate: String? = null,
    var entryList: String? = null,
    var pageNumber: String? = null,
    var searchKey: String = "",
    var startDate: String? = null
)