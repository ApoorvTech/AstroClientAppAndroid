package com.myastrotell.ui.chatrequest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ChatApiKeys
import com.myastrotell.data.DataManager
import com.myastrotell.pojo.response.StartChatResponse
import kotlinx.coroutines.launch


class ChatRequestViewModel : BaseViewModel() {

    private val mRepo = ChatRequestRepo()

    val initChatLiveData by lazy { MutableLiveData<BaseResponseModel<StartChatResponse>>() }


    /**
     * method to initialize chat
     */
    fun initChat(to: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val paramMap = HashMap<String, String>()
                paramMap.put(ChatApiKeys.appName, BuildConfig.APP_NAME)
                paramMap.put(ChatApiKeys.from, DataManager.getMsisdn())
                paramMap.put(ChatApiKeys.to, to)

                mRepo.initChat(
                    ApiRequestCodes.START_CHAT,
                    paramMap,
                    initChatLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }

}