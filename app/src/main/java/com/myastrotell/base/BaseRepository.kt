package com.myastrotell.base

import com.google.gson.Gson
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.data.NetworkStatusMessage
import com.myastrotell.data.restapi.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


open class BaseRepository {

    suspend fun <T> safeApiCall(
        apiRequestCode: Int,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T
    ): ResultWrapper<T?> {
        return withContext(dispatcher) {
            try {

                val response = apiCall.invoke()

                if (response is BaseResponseModel<*>) {

                    response.apiRequestCode = apiRequestCode

                    if (response.code == ApiStatusCodes.SUCCESS) {
                        ResultWrapper.Success(response as T)

                    } else {
                        ResultWrapper.GenericError(response)
                    }

                } else if (response is Response<*> && !response.isSuccessful) {
                    val errorResponse = Gson().fromJson(
                        response.errorBody().toString(), BaseResponseModel::class.java
                    )

                    errorResponse?.apiRequestCode = apiRequestCode

                    ResultWrapper.GenericError(errorResponse)

                } else {
                    val errorResponse = getDefaultErrorObject()
                    errorResponse.apiRequestCode = apiRequestCode
                    errorResponse.msg = NetworkStatusMessage.SERVER_ERROR.message
                    ResultWrapper.GenericError(errorResponse)
                }

            } catch (throwable: Throwable) {

                when (throwable) {
                    is SocketTimeoutException,
                    is SocketException,
                    is ConnectException,
                    is TimeoutException,
                    is UnknownHostException
                    -> {
                        val errorResponse = getDefaultErrorObject()
                        errorResponse.apiRequestCode = apiRequestCode
                        errorResponse.msg = NetworkStatusMessage.CONNECTION_ERROR.message
                        ResultWrapper.GenericError(errorResponse)
                    }

                    is HttpException -> {
                        val errorResponse = getDefaultErrorObject()
                        errorResponse.apiRequestCode = apiRequestCode
                        errorResponse.code = throwable.response()?.code()!!
                        errorResponse.msg =
                            throwable.localizedMessage ?: NetworkStatusMessage.SERVER_ERROR.message
                        ResultWrapper.GenericError(errorResponse)
                    }

                    else -> {
                        val errorResponse = getDefaultErrorObject()
                        errorResponse.apiRequestCode = apiRequestCode
                        errorResponse.msg =
                            throwable.localizedMessage ?: NetworkStatusMessage.SERVER_ERROR.message
                        ResultWrapper.GenericError(errorResponse)
                    }
                }
            }
        }
    }

    private fun getDefaultErrorObject(): BaseResponseModel<*> {
        return BaseResponseModel<Any>()
    }

}