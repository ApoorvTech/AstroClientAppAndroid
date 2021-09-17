package com.myastrotell.ui.termsandpolicy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.TermsAndPolicyResponse
import kotlinx.coroutines.launch


class TermsAndPolicyViewModel : BaseViewModel() {
    private val mRepo = TermsAndPolicyRepo()

    val termsAndPolicyLiveData by lazy { MutableLiveData<BaseResponseModel<TermsAndPolicyResponse>>() }


    fun getTermsAndPolicyData() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getTermsAndPolicyData(
                    ApiRequestCodes.TERMS_POLICY,
                    termsAndPolicyLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


}