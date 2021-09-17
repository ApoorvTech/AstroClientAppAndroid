package com.myastrotell.ui.detailform

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.KeyDataType
import com.myastrotell.data.database.entities.IntakeFormEntity
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.DetailFormSelectionModel
import com.myastrotell.pojo.requests.*
import com.myastrotell.pojo.response.CallRequestResponse
import com.myastrotell.pojo.response.StartChatResponse

class DetailsFormRepo : BaseRepository() {


    suspend fun getIntakeFormData(): IntakeFormEntity? {
        return DataManager.getIntakeFormData()
    }

    suspend fun getSavedProfileData(): UserProfileEntity? {
        return DataManager.getProfileData()
    }

    suspend fun addIntakeFormData(data: IntakeFormEntity) {
        DataManager.addIntakeForm(data)
    }


    suspend fun getConcerns(
        apiRequestCode: Int,
        concernsResponse: MutableLiveData<List<DetailFormSelectionModel>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        if (!DataManager.detailsFormConcerns.isNullOrEmpty()) {
            concernsResponse.value = DataManager.detailsFormConcerns!!
            return
        }

        val request = KeyDataRequest(listOf(KeyDataType.TOPIC.value))

        safeApiCall(apiRequestCode) {

            DataManager.getKeyValue(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {

                    val concernsList = ArrayList<DetailFormSelectionModel>()

                    it.response?.data?.let {
                        for (data in it) {
                            concernsList.add(DetailFormSelectionModel(data.value, false))
                        }
                    }

                    DataManager.detailsFormConcerns = concernsList

                    concernsResponse.value = concernsList

                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun saveIntakeFrom(
        apiRequestCode: Int,
        request: IntakeFormRequest,
        saveResponse: MutableLiveData<BaseResponseModel<List<IntakeFormFieldModel>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.saveIntakeForm(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    saveResponse.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    // UnUsed
    suspend fun sendChatRequest(
        apiRequestCode: Int,
        paramsMap: HashMap<String, String>,
        startChatResponse: MutableLiveData<BaseResponseModel<StartChatResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.sendChatRequest(paramsMap)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    startChatResponse.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun initChat(
        apiRequestCode: Int,
        paramsMap: HashMap<String, String>,
        startChatResponse: MutableLiveData<BaseResponseModel<StartChatResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.startSocketChat(paramsMap)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    startChatResponse.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun orderReport(
        apiRequestCode: Int,
        request: RedeemPointsRequest,
        astrologersbusyListLiveDate: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.redeemPoints(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {

                    val balance =
                        DataManager.getStringFromPreference(PreferenceManager.WALLET_BALANCE)

                    if (balance.isNotBlank()) {
                        val updatedBalance = balance.toDouble() - request.redeemPoint.toDouble()

                        // updating wallet balance
                        DataManager.saveStringInPreference(
                            PreferenceManager.WALLET_BALANCE, updatedBalance.toString()
                        )
                    }

                    astrologersbusyListLiveDate.value = it.response

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

    suspend fun clearIntakeFormData() {
        DataManager.clearIntakeFormData()
    }


}