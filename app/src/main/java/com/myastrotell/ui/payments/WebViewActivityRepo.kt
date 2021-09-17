package com.myastrotell.ui.payments

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.payment.InitiatePaymentModel
import com.myastrotell.pojo.requests.payment.InitiatePaymentResponse


object WebViewActivityRepo :BaseRepository(){

    suspend fun getRsaKey(data: HashMap<String, String>, apiRequestCode: Int, notificationResponse: MutableLiveData<String>, errorLiveData: MutableLiveData<BaseResponseModel<*>>) {
        safeApiCall(apiRequestCode) {
            DataManager.getRsaKey(data)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    notificationResponse.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }
    suspend fun initiatePayment(requestModel: InitiatePaymentModel, apiRequestCode: Int, responseData: MutableLiveData<InitiatePaymentResponse>, errorLiveData: MutableLiveData<BaseResponseModel<*>>) {
        safeApiCall(apiRequestCode) {
            DataManager.initiatePayment(requestModel)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    responseData.value = it.response?.data
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }



}