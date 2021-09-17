package com.myastrotell.ui.orderhistory

import androidx.lifecycle.MutableLiveData
import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.RedemptionType
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.CallRequest
import com.myastrotell.pojo.requests.ProductBillingDetailRequest
import com.myastrotell.pojo.requests.ProductBillingRequest
import com.myastrotell.pojo.requests.RedemptionSearchRequest
import com.myastrotell.pojo.response.*


class OrderHistoryRepo : BaseRepository() {

    suspend fun getOrderHistory(
        apiRequestCode: Int,
        request: ProductBillingRequest,
        orderHistoryLiveData: MutableLiveData<BaseResponseModel<List<ProductBillingDetails>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getProductBillingList(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    orderHistoryLiveData.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getOrderDetails(
        apiRequestCode: Int,
        orderId: String,
        orderHistoryLiveData: MutableLiveData<BaseResponseModel<OrderDetailResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = ProductBillingDetailRequest(orderId)

        safeApiCall(apiRequestCode) {

            DataManager.getProductBillingDetails(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    orderHistoryLiveData.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getAllAstrologersStatus(
        apiRequestCode: Int,
        astrologersStatusListLiveDate: MutableLiveData<BaseResponseModel<List<AstrologersStatusResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getAllAstrologersStatus()

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    astrologersStatusListLiveDate.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getBusyAstrologersList(
        apiRequestCode: Int,
        astrologersbusyListLiveDate: MutableLiveData<BaseResponseModel<List<String>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getBusyAstrologersList(BuildConfig.APP_NAME)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    astrologersbusyListLiveDate.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }




    suspend fun getAstrologerStatusByNumber(
        apiRequestCode: Int,
        astrologerMsisdn: String,
        busyAstrologersListLiveData: MutableLiveData<BaseResponseModel<AstrologerStatusByMsisdnResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getAstrologerStatusByNumber(astrologerMsisdn)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    busyAstrologersListLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getReportStatusForUser(
        apiRequestCode: Int,
        reportStatusListLiveData: MutableLiveData<BaseResponseModel<List<OrderDetailResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = RedemptionSearchRequest(RedemptionType.REPORT.value, DataManager.getMsisdn())

        safeApiCall(apiRequestCode) {

            DataManager.getReportStatusForUser(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    reportStatusListLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun callRequest(
        apiRequestCode: Int,
        request: CallRequest,
        callRequestResponse: MutableLiveData<BaseResponseModel<CallRequestResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.callRequest(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    callRequestResponse.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


}