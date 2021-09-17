package com.myastrotell.pojo.response

data class UploadFileResponse(
    var campaignId: Int?,
    var clientId: String?,
    var displayTitle: String?,
    var id: String?,
    var imageUrl: String?,
    var mimeType: String?,
    var msisdn: String?,
    var originalName: String?,
    var updateTimestamp: String?,
    var uploadCategory: String?,
    var uploadDate: String?
)