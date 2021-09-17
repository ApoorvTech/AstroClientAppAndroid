package com.myastrotell.pojo.response

data class AstrologersStatusResponse(
    var id: Int?,
    var notification: Int?,
    var productId: String?,
    var productType: String?,
    var saleEndTime: Long?,
    var saleStartTime: Long?
)