package com.myastrotell.ui.support

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import kotlinx.coroutines.launch


class SupportViewModel : BaseViewModel() {
    private val mRepo = SupportRepo()

    val formSubmissionLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }


    fun getName() = ("${mRepo.getFirstName()} ${mRepo.getLastName()}").toString().trim()

    fun getMsisdn(): String {
        val msisdn = mRepo.getMsisdn()
        return if (msisdn.length >= 10)
            msisdn
        else
            ""
    }


    fun submitFormData(name: String?, number: String?, email: String?, concern: String?) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.submitFormData(
                    ApiRequestCodes.SUBMIT_SUPPORT_FORM,
                    name,
                    number,
                    email,
                    concern,
                    formSubmissionLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }

}