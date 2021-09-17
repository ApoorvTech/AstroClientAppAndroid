package com.myastrotell.ui.payments.paymentgateway

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.payment.InitiatePaymentModel
import com.myastrotell.pojo.requests.payment.InitiatePaymentResponse


object PaymentRepo : BaseRepository() {

    suspend fun initiatePayment(
        requestModel: InitiatePaymentModel,
        apiRequestCode: Int,
        responseData: MutableLiveData<InitiatePaymentResponse>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
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




    suspend fun getRechargeDetails(
        msisdn: String,
        apiRequestCode: Int,
        responseData: MutableLiveData<Boolean>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
        safeApiCall(apiRequestCode) {
            DataManager.getRechargeDetails(msisdn)

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