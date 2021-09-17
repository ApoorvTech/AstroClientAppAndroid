package com.myastrotell.ui.support

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.SubmitSupportFormRequest


class SupportRepo : BaseRepository() {

    fun getFirstName() = DataManager.getStringFromPreference(PreferenceManager.FIRST_NAME)

    fun getLastName() = DataManager.getStringFromPreference(PreferenceManager.LAST_NAME)

    fun getMsisdn() = DataManager.getStringFromPreference(PreferenceManager.MSISDN)


    suspend fun submitFormData(
        apiRequestCode: Int,
        name: String?,
        number: String?,
        email: String?,
        concern: String?,
        formSubmissionLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
        val request = SubmitSupportFormRequest(
            name,
            number,
            email,
            concern
        )

        safeApiCall(apiRequestCode) {

            DataManager.submitSupportForm(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    formSubmissionLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


}