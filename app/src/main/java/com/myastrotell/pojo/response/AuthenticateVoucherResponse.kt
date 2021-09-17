package com.myastrotell.pojo.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthenticateVoucherResponse(
    var id: String?,
    var campaignId: String?,
    var voucher_transaction_id: String?,
    var clientId: String?,
    var promocode: String?,
    var promocodeCategory: String?,
    var basedOn: String?,
    var useType: String?,
    var expiryDays: String?,
    var promocodeFigure: Double?,
    var minAmount: Double,
    var maxAmount: String?,
    var startDate: String?,
    var endDate: String?,
    var channel: String?
) : Parcelable