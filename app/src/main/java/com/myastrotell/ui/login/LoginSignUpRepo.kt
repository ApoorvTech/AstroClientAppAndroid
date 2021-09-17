package com.myastrotell.ui.login

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.Mode
import com.myastrotell.data.ProfileFieldColumn
import com.myastrotell.data.UserRole
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.*
import com.myastrotell.pojo.response.LoginResponse
import com.myastrotell.pojo.response.ValidateOtpResponse
import com.myastrotell.utils.encryption.Encryption


class LoginSignUpRepo : BaseRepository() {

    fun setGuestDetails() {
        DataManager.setMsisdn("0")
        DataManager.saveStringInPreference(PreferenceManager.ACCESS_TOKEN, "")
        DataManager.saveBooleanInPreference(PreferenceManager.IS_LOGGED_IN, false)
        DataManager.saveBooleanInPreference(PreferenceManager.IS_GUEST_USER, true)
        setTutorialsShown(true)
    }


    fun setTutorialsShown(isShown: Boolean) {
        DataManager.saveBooleanInPreference(PreferenceManager.ARE_TUTORIALS_SHOWN, isShown)
    }


    suspend fun checkProfileStatus(
        apiRequestCode: Int,
        checkStatusLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        number: String
    ) {
        // saving msisdn for header
        DataManager.setMsisdn(number)

        val request = CheckNumberRequest(Mode.MOBILE.value, number)

        safeApiCall(apiRequestCode) {
            DataManager.checkProfileStatus(request)
        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    checkStatusLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun sendOTP(
        apiRequestCode: Int,
        sendOtpLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        number: String
    ) {
        val request = SendOTP(number)

        safeApiCall(apiRequestCode) {

            DataManager.sendOTP(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    sendOtpLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun validateOtp(
        apiRequestCode: Int,
        validateOtpLiveData: MutableLiveData<BaseResponseModel<ValidateOtpResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        otp: String
    ) {
        val request = ValidateOTP(DataManager.getMsisdn(), otp)

        safeApiCall(apiRequestCode) {

            DataManager.validateOTP(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {

                    it.response?.data?.let { data ->
                        // saving accessToken
                        DataManager.saveStringInPreference(
                            PreferenceManager.ACCESS_TOKEN, data.accessToken ?: ""
                        )
                    }

                    validateOtpLiveData.value = it.response

                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun sendOtpForResetPassword(
        apiRequestCode: Int,
        sendOtpLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        number: String
    ) {
        val request = SendOtpForResetPassword(userName = number)

        safeApiCall(apiRequestCode) {

            DataManager.sendOtpForResetPassword(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    sendOtpLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun validateOtpForResetPassword(
        apiRequestCode: Int,
        validateOtpLiveData: MutableLiveData<BaseResponseModel<ValidateOtpResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        otp: String
    ) {
        val request = ValidateOTPForResetPassword(userName = DataManager.getMsisdn(), otp = otp)

        safeApiCall(apiRequestCode) {

            DataManager.validateOtpForResetPassword(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {

                    it.response?.data?.let { data ->
                        // saving accessToken
                        DataManager.saveStringInPreference(
                            PreferenceManager.ACCESS_TOKEN, data.accessToken ?: ""
                        )
                    }

                    validateOtpLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun login(
        apiRequestCode: Int,
        loginLiveData: MutableLiveData<BaseResponseModel<LoginResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        event: MutableLiveData<Boolean>,
        password: String
    ) {

        val request = LoginRequest(
            DataManager.getMsisdn(),
            Encryption.encrypt(password) ?: ""
        )

        safeApiCall(apiRequestCode) {

            DataManager.login(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    it.response?.data?.let { data ->
                        DataManager.saveBooleanInPreference(PreferenceManager.IS_LOGGED_IN, true)
                        DataManager.saveBooleanInPreference(PreferenceManager.IS_GUEST_USER, false)

                        DataManager.saveStringInPreference(
                            PreferenceManager.ACCESS_TOKEN, data.accessToken ?: ""
                        )

                    }

                    event.value=true
                    loginLiveData.value = it.response

                }

                is ResultWrapper.GenericError -> {
                    event.value=false
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun createUserProfile(
        apiRequestCode: Int,
        validateOtpLiveData: MutableLiveData<BaseResponseModel<ArrayList<UserProfileDetailRequest>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        password: String
    ) {

        val dataList = ArrayList<ProfileDetailData>()

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.MSISDN.value,
                fieldColumnName = ProfileFieldColumn.MSISDN.value,
                fieldValue = DataManager.getMsisdn(),
                minLength = 10,
                maxLength = 10
            )
        )

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.USERNAME.value,
                fieldColumnName = ProfileFieldColumn.USERNAME.value,
                fieldValue = DataManager.getMsisdn(),
                minLength = 0,
                maxLength = 10
            )
        )

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.PASSWORD.value,
                fieldColumnName = ProfileFieldColumn.PASSWORD.value,
                fieldValue = Encryption.encrypt(password) ?: "",
                minLength = 5,
                maxLength = 50
            )
        )

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.USER_CATEGORY.value,
                fieldColumnName = ProfileFieldColumn.USER_CATEGORY.value,
                fieldValue = UserRole.ROLE_CLIENT.value,
                minLength = 5,
                maxLength = 20
            )
        )

        val request = UserProfileDetailRequest(dataList)

        safeApiCall(apiRequestCode) {

            DataManager.createUserProfile(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    it.response?.data?.let { data ->
                        DataManager.saveBooleanInPreference(PreferenceManager.IS_LOGGED_IN, true)
                        DataManager.saveBooleanInPreference(PreferenceManager.IS_GUEST_USER, false)
                    }
                    validateOtpLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun resetPassword(
        apiRequestCode: Int,
        resetPasswordLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        password: String
    ) {

        val dataList = ArrayList<ProfileDetailData>()

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.MSISDN.value,
                fieldColumnName = ProfileFieldColumn.MSISDN.value,
                fieldValue = DataManager.getMsisdn(),
                minLength = 10,
                maxLength = 10
            )
        )

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.USERNAME.value,
                fieldColumnName = ProfileFieldColumn.USERNAME.value,
                fieldValue = DataManager.getMsisdn(),
                minLength = 0,
                maxLength = 10
            )
        )

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.PASSWORD.value,
                fieldColumnName = ProfileFieldColumn.PASSWORD.value,
                fieldValue = Encryption.encrypt(password) ?: "",
                minLength = 5,
                maxLength = 50
            )
        )

        dataList.add(
            ProfileDetailData(
                displayTitle = ProfileFieldColumn.USER_CATEGORY.value,
                fieldColumnName = ProfileFieldColumn.USER_CATEGORY.value,
                fieldValue = UserRole.ROLE_CLIENT.value,
                minLength = 5,
                maxLength = 20
            )
        )

        val request = UserProfileDetailRequest(dataList)

        safeApiCall(apiRequestCode) {

            DataManager.resetPassword(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    resetPasswordLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


}