package com.myastrotell.pojo.requests.buymallproduct

data class ShippingAddress(
    val deliveryName:String,
    val deliveryMobileNo:String,
    val houseNo:String?=null,
    val addressLine1:String,
    val addressLine2:String?,
    val pinCode:String,
    val city:String,
    val state:String,
    val country:String
)