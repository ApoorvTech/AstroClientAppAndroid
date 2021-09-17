package com.myastrotell.ui.chatrequest

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.response.StartChatResponse


class ChatRequestRepo : BaseRepository() {

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

}