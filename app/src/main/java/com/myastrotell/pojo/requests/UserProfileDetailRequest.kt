package com.myastrotell.pojo.requests


data class UserProfileDetailRequest(val data: ArrayList<ProfileDetailData>)

data class ProfileDetailData(
    var displayTitle: String,
    var isMandatory: Int = 1,
    var isEditable: Int = 1,
    var controlType: String = "TEXT",
    var fieldValue: String?,
    var fieldColumnName: String,
    var dataValues: String? = null,
    var sequence: Int = 1,
    var mode: String = "API",
    var minLength: Int = 0,
    var maxLength: Int = 10

)