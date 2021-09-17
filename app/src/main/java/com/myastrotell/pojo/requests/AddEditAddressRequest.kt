package com.myastrotell.pojo.requests


data class AddEditAddressRequest(var userAddressList: List<AddressRequest>)


data class AddressRequest(
    var addressId: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var mobile: String? = null,
    var houseNo: String? = null,
    var addressLine1: String? = null,
    var addressLine2: String? = null,
    var state: String? = null,
    var city: String? = null,
    var country: String? = null,
    var pinCode: String? = null,
    var addressCategory: String? = "Address",
    var addressType: String? = "address"
)