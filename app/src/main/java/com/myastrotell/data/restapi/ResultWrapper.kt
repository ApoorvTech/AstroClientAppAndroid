package com.myastrotell.data.restapi

import com.myastrotell.base.BaseResponseModel

sealed class ResultWrapper<out T> {
//    val data: T? = null
    data class Success<out T>(val response: T?) : ResultWrapper<T?>()
    data class GenericError(val response: BaseResponseModel<*>? = null) : ResultWrapper<Nothing>()
}