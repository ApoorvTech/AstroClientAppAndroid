package com.myastrotell.pojo.response.astrlogerprofile


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ReviewModel(
    @SerializedName("campaignId")
    var campaignId: Int?,
    @SerializedName("clientId")
    var clientId: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("language")
    var language: String?,
    @SerializedName("msisdn")
    var msisdn: String?,
    @SerializedName("productId")
    var productId: String?,
    @SerializedName("rating")
    var rating: Int?,
    @SerializedName("review")
    var review: String?,
    @SerializedName("reviewerName")
    var reviewerName: String?,
    @SerializedName("updateTimestamp")
    var updateTimestamp: String?
)