package com.myastrotell.ui.home

import androidx.lifecycle.MutableLiveData
import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ChatApiKeys
import com.myastrotell.data.DataManager
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.RegisterFcmIdRequest
import com.myastrotell.pojo.response.HomeDataResponse
import com.myastrotell.pojo.response.HomeStaticDataResponse
import com.myastrotell.pojo.response.UserProfileResponse
import com.myastrotell.pojo.response.userchatstatus.ChatStatusResposne


class HomeRepo : BaseRepository() {

    fun getFcmID() = DataManager.getStringFromPreference(PreferenceManager.FCM_ID)

    fun getFirstName() = DataManager.getStringFromPreference(PreferenceManager.FIRST_NAME)

    fun getLastName() = DataManager.getStringFromPreference(PreferenceManager.LAST_NAME)

    fun getProfileImage() = DataManager.getStringFromPreference(PreferenceManager.PROFILE_IMAGE)

    fun getMsisdn() = DataManager.getStringFromPreference(PreferenceManager.MSISDN)


    suspend fun saveProfileData(entity: UserProfileEntity) {
        DataManager.saveStringInPreference(PreferenceManager.FIRST_NAME, entity.firstName ?: "")
        DataManager.saveStringInPreference(PreferenceManager.LAST_NAME, entity.lastName ?: "")
        DataManager.saveStringInPreference(
            PreferenceManager.PROFILE_IMAGE, entity.profilePicUrl ?: ""
        )
        // saving in DB
        DataManager.saveProfileData(entity)
    }


    suspend fun getHomeData(
        apiRequestCode: Int,
        homeFeaturesLiveData: MutableLiveData<BaseResponseModel<HomeDataResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
        safeApiCall(apiRequestCode) {

            DataManager.getHomeData()

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    homeFeaturesLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun getHomeStaticData(
        apiRequestCode: Int,
        homeFeaturesLiveData: MutableLiveData<BaseResponseModel<List<HomeStaticDataResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
        safeApiCall(apiRequestCode) {

            DataManager.getHomeStaticData()

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    homeFeaturesLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun registerFcmId(
        apiRequestCode: Int,
        registerFcmIdLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = RegisterFcmIdRequest(getFcmID())

        safeApiCall(apiRequestCode) {

            DataManager.registerFcmId(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    registerFcmIdLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun getWalletBalance(
        apiRequestCode: Int
    ) {
        safeApiCall(apiRequestCode) {

            DataManager.getWalletBalance()

        }.let {
            if (it is ResultWrapper.Success) {
                it.response?.data?.myPoint?.point?.let { points ->
                    DataManager.saveStringInPreference(PreferenceManager.WALLET_BALANCE, points)
                }
            }
        }
    }


    suspend fun getChatStatusForUser(
        apiRequestCode: Int,
        chatStatusLiveData: MutableLiveData<BaseResponseModel<List<ChatStatusResposne>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val params = HashMap<String, String>()
        params[ChatApiKeys.appName] = BuildConfig.APP_NAME
        params[ChatApiKeys.to] = DataManager.getMsisdn()

        safeApiCall(apiRequestCode) {

            DataManager.getChatStatusForUser(params)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    chatStatusLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun getUserProfileDetails(
        apiRequestCode: Int,
        mProfileLiveData: MutableLiveData<BaseResponseModel<UserProfileResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getUserProfileDetails()

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    mProfileLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }



    suspend fun endChat(
        apiRequestCode: Int,
        paramsMap: HashMap<String, String>,
    ): ResultWrapper<BaseResponseModel<Any>?> {
        return safeApiCall(apiRequestCode) {
            DataManager.endSocketChat(paramsMap)
        }

    }

}