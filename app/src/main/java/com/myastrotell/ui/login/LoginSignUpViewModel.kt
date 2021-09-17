package com.myastrotell.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.requests.UserProfileDetailRequest
import com.myastrotell.pojo.response.LoginResponse
import com.myastrotell.pojo.response.ValidateOtpResponse
import kotlinx.coroutines.launch


class LoginSignUpViewModel : BaseViewModel() {
    private val mRepo = LoginSignUpRepo()


    val checkStatusLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }

    val sendOtpLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }

    val validateOtpLiveData by lazy { MutableLiveData<BaseResponseModel<ValidateOtpResponse>>() }

    val loginLiveData by lazy { MutableLiveData<BaseResponseModel<LoginResponse>>() }
    val eventTrackeLiveData by lazy { MutableLiveData<Boolean>() }

    val createUserProfileLiveData by lazy { MutableLiveData<BaseResponseModel<ArrayList<UserProfileDetailRequest>>>() }

    val resetPasswordLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }


    fun setGuestDetails() {
        mRepo.setGuestDetails()
    }


    fun setTutorialsShown(isShown: Boolean) {
        mRepo.setTutorialsShown(isShown)
    }


    fun checkProfileStatus(number: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.checkProfileStatus(
                    ApiRequestCodes.CHECK_PROFILE_STATUS, checkStatusLiveData, errorLiveData, number
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun sendOtp(number: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.sendOTP(
                    ApiRequestCodes.SEND_OTP,
                    sendOtpLiveData,
                    errorLiveData,
                    number
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun validateOtp(otp: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.validateOtp(
                    ApiRequestCodes.VALIDATE_OTP,
                    validateOtpLiveData,
                    errorLiveData,
                    otp
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun sendOtpForResetPassword(number: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.sendOtpForResetPassword(
                    ApiRequestCodes.RESET_PASSWORD_SEND_OTP,
                    sendOtpLiveData,
                    errorLiveData,
                    number
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun validateOtpForResetPassword(otp: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.validateOtpForResetPassword(
                    ApiRequestCodes.RESET_PASSWORD_VALIDATE_OTP,
                    validateOtpLiveData,
                    errorLiveData,
                    otp
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun login(password: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.login(
                    ApiRequestCodes.LOGIN,
                    loginLiveData,
                    errorLiveData,
                    eventTrackeLiveData,
                    password
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun createUserProfile(password: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.createUserProfile(
                    ApiRequestCodes.ENTER_PASSWORD,
                    createUserProfileLiveData,
                    errorLiveData,
                    password
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun resetPassword(password: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.resetPassword(
                    ApiRequestCodes.ENTER_PASSWORD,
                    resetPasswordLiveData,
                    errorLiveData,
                    password
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


}