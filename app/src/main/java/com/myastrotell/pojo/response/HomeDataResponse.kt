package com.myastrotell.pojo.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class HomeDataResponse(
    var bannerList: List<Banner>?,
    var featureList: List<Feature>?
)


data class Banner(
    var bannerId: Int?,
    var bannerName: String?,
    var bannerType: String?,
    var bannerURL: String?,
    var clientId: String?,
    var description: String?,
    var isActive: String?,
    var language: String?,
    var redirectURL: String?,
    var sequence: String?
)


@Parcelize
data class Feature(
    var clientId: String?,
    var featureID: Int?,
    var featureIcon: String?,
    var featureName: String?,
    var functionKey: Int?,
    var language: String?,
    var layout: String?,
    var sequence: Int?,
    var isSelected: Boolean = false
) : Parcelable