package com.myastrotell.ui.termsandpolicy

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.response.TermsAndPolicyResponse


class TermsAndPolicyRepo : BaseRepository() {

    suspend fun getTermsAndPolicyData(
        apiRequestCode: Int,
        homeFeaturesLiveData: MutableLiveData<BaseResponseModel<TermsAndPolicyResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getTermsAndPolicy()

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

}