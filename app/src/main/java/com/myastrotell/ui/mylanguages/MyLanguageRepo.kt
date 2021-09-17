package com.myastrotell.ui.mylanguages

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.KeyDataType
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.KeyDataRequest
import com.myastrotell.pojo.response.KeyDataResponse


class MyLanguageRepo : BaseRepository() {


    suspend fun getSelectedLanguages() = DataManager.getSelectedLanguages()

    suspend fun clearLanguages() = DataManager.clearLanguages()

    suspend fun addLanguage(entity: SelectedLanguagesEntity) = DataManager.addLanguage(entity)


    suspend fun getLanguageList(
        apiRequestCode: Int,
        languageListLiveData: MutableLiveData<List<KeyDataResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
        if (!DataManager.languageList.isNullOrEmpty()) {
            languageListLiveData.value = DataManager.languageList
            return
        }

        val request = KeyDataRequest(listOf(KeyDataType.LANGUAGE.value))

        safeApiCall(apiRequestCode) {

            DataManager.getKeyValue(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    it.response?.data?.let { list ->
                        DataManager.languageList = list
                        languageListLiveData.value = list
                    }
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


}