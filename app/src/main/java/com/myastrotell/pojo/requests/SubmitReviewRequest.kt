package com.myastrotell.pojo.requests

data class SubmitReviewRequest(
    var productId: String?,
    var rating: String?,
    var review: String?,
    var reviewerName: String?
)