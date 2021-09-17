package com.myastrotell.ui.chat

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.database.entities.IntakeFormEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.ProductBillingDetailRequest
import com.myastrotell.pojo.requests.SubmitReviewRequest
import com.myastrotell.pojo.response.MessageResponse
import com.myastrotell.pojo.response.OrderDetailResponse

class ChatRepo : BaseRepository() {

    fun getMsisdn() = DataManager.getMsisdn()

    fun getUserName() = ("${DataManager.getStringFromPreference(PreferenceManager.FIRST_NAME)} ${
        DataManager.getStringFromPreference(PreferenceManager.LAST_NAME)
    }").trim()

    fun getChatStartTime() = DataManager.getStringFromPreference(PreferenceManager.CHAT_START_TIME)

    fun setChatStartTime(time: String?) {
        DataManager.saveStringInPreference(PreferenceManager.CHAT_START_TIME, time ?: "")
    }

    fun setUserBusy(isBusy: Boolean = false) {
        DataManager.saveBooleanInPreference(PreferenceManager.IS_USER_BUSY, isBusy)
    }


    suspend fun getIntakeFormData(): IntakeFormEntity? {
        return DataManager.getIntakeFormData()
    }


    suspend fun endChat(
        apiRequestCode: Int,
        paramsMap: HashMap<String, String>,
        endChatResponse: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.endSocketChat(paramsMap)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    endChatResponse.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun sendMessage(
        apiRequestCode: Int,
        paramsMap: HashMap<String, String>,
        sendChatResponse: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.sendMessage(paramsMap)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    sendChatResponse.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getChatHistory(
        apiRequestCode: Int,
        paramMap: HashMap<String, String>,
        newChatsResponse: MutableLiveData<BaseResponseModel<List<MessageResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {


        safeApiCall(apiRequestCode) {

            DataManager.getChatHistory(paramMap)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    newChatsResponse.value = it.response
                }

                /*is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }*/
            }
        }

    }

    suspend fun newChats(
        apiRequestCode: Int,
        paramMap: HashMap<String, String>,
        newChatsResponse: MutableLiveData<BaseResponseModel<List<MessageResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.newChats(paramMap)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    newChatsResponse.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }

    suspend fun getOrderDetails(
        apiRequestCode: Int,
        orderId: String,
        orderHistoryLiveData: MutableLiveData<BaseResponseModel<OrderDetailResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = ProductBillingDetailRequest(orderId)

        safeApiCall(apiRequestCode) {

            DataManager.getProductBillingDetails(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    orderHistoryLiveData.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun submitReview(
        apiRequestCode: Int,
        id: String,
        rating: Float,
        review: String,
        name: String,
        orderHistoryLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = SubmitReviewRequest(id, rating.toInt().toString(), review, name)

        safeApiCall(apiRequestCode) {

            DataManager.submitReview(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    orderHistoryLiveData.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }

}