package com.myastrotell.base


data class BaseResponseModel<T>(
    var apiRequestCode: Int = 0,
    var status: String? = null,
    var msg: String? = null,
    var code: Int = 0,
    var data: T? = null
)
