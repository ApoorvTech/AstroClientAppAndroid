package com.myastrotell.ui.astrologerslist

import androidx.lifecycle.MutableLiveData
import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.RedemptionType
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.CallRequest
import com.myastrotell.pojo.requests.RedemptionSearchRequest
import com.myastrotell.pojo.requests.SubscribeForAvailableStatusRequest
import com.myastrotell.pojo.requests.SubscribeForOnlineStatusRequest
import com.myastrotell.pojo.response.AstrologerListResponse
import com.myastrotell.pojo.response.AstrologerStatusByMsisdnResponse
import com.myastrotell.pojo.response.CallRequestResponse
import com.myastrotell.pojo.response.OrderDetailResponse


class AstrologersListRepo : BaseRepository() {


    suspend fun getSelectedLanguages(): List<SelectedLanguagesEntity>? {
        return DataManager.getSelectedLanguages()
    }

    fun isUserBusy(): Boolean {
        return DataManager.getBooleanFromPreference(PreferenceManager.IS_USER_BUSY)
    }


    suspend fun getAstrologersList(
        apiRequestCode: Int,
        astrologersListLiveData: MutableLiveData<BaseResponseModel<ArrayList<AstrologerListResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        type: String
    ) {
        val request = RedemptionSearchRequest(type, RedemptionType.WHITE.value)

        safeApiCall(apiRequestCode) {

            DataManager.getAstrologersList(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    astrologersListLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getBusyAstrologersList(
        apiRequestCode: Int,
        busyAstrologersListLiveData: MutableLiveData<BaseResponseModel<List<String>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getBusyAstrologersList(BuildConfig.APP_NAME)

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


    suspend fun subscribeForOnlineStatus(
        apiRequestCode: Int,
        productId: String?, type: String,
        reportStatusListLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = SubscribeForOnlineStatusRequest(productType = type, productId = productId)

        safeApiCall(apiRequestCode) {

            DataManager.subscribeForOnlineStatus(request)

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


    suspend fun subscribeForAvailableStatus(
        apiRequestCode: Int,
        productId: String?, type: String,
        reportStatusListLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = SubscribeForAvailableStatusRequest(productType = type, productId = productId)

        safeApiCall(apiRequestCode) {

            DataManager.subscribeForAvailableStatus(request)

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