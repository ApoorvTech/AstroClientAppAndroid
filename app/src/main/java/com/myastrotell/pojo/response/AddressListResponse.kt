package com.myastrotell.pojo.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressListResponse(
    var addressCategory: String?,
    var addressId: String?,
    var addressLine1: String?,
    var addressLine2: String?,
    var addressType: String?,
    var city: String?,
    var country: String?,
    var houseNo: String?,
    var mobile: String?,
    var pinCode: String?,
    var state: String?,
    var userId: String?,
    var userName: String?
) : Parcelable