package com.myastrotell.ui.astrologerprofile

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.RedemptionType
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.CallRequest
import com.myastrotell.pojo.requests.ProductInfoRequest
import com.myastrotell.pojo.requests.RedemptionSearchRequest
import com.myastrotell.pojo.requests.ReviewRequest
import com.myastrotell.pojo.response.AstrologerStatusByMsisdnResponse
import com.myastrotell.pojo.response.AstrologersStatusResponse
import com.myastrotell.pojo.response.CallRequestResponse
import com.myastrotell.pojo.response.astrlogerprofile.AstrologerProfileData
import com.myastrotell.pojo.response.astrlogerprofile.AstrologerProfileWithRateResponse
import com.myastrotell.pojo.response.astrlogerprofile.ItemInfo
import com.myastrotell.pojo.response.astrlogerprofile.ReviewModel


class AstrologerProfileRepo : BaseRepository() {

    fun isUserBusy(): Boolean {
        return DataManager.getBooleanFromPreference(PreferenceManager.IS_USER_BUSY)
    }


    suspend fun getAstrologersDetails(
        apiRequestCode: Int,
        astrologersListLiveDate: MutableLiveData<BaseResponseModel<AstrologerProfileData>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        msisdn: String
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getAstrologerDetails(msisdn)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    astrologersListLiveDate.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getAstrologersDetailsWithRate(
        apiRequestCode: Int,
        astrologersListLiveDate: MutableLiveData<BaseResponseModel<List<AstrologerProfileWithRateResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        msisdn: String
    ) {

        val request = RedemptionSearchRequest(
            msisdn,
            RedemptionType.WHITE.value
        )

        safeApiCall(apiRequestCode) {

            DataManager.getAstrologerDetailsWithRate(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    astrologersListLiveDate.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getProductTimeDetails(
        apiRequestCode: Int,
        astrologersListLiveDate: MutableLiveData<BaseResponseModel<List<ItemInfo>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        msisdn: String
    ) {

        val request = ProductInfoRequest(titleType = RedemptionType.WHITE.value, titleKey = msisdn)

        safeApiCall(apiRequestCode) {
            DataManager.getProductInfo(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    astrologersListLiveDate.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }

    suspend fun getAstroReview(
        apiRequestCode: Int,
        astrologersListLiveDate: MutableLiveData<BaseResponseModel<List<ReviewModel>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        msisdn: String
    ) {
        val request = ReviewRequest(msisdn)

        safeApiCall(apiRequestCode) {

            DataManager.getReviewList(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    astrologersListLiveDate.value = it.response
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