package com.myastrotell.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.myastrotell.BaseApplication
import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ChatApiKeys
import com.myastrotell.data.DataManager
import com.myastrotell.data.UserRole
import com.myastrotell.pojo.requests.SendMessageRequest
import com.myastrotell.pojo.response.MessageResponse
import com.myastrotell.pojo.response.OrderDetailResponse
import com.myastrotell.utils.ConnectionLiveData
import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent


class ChatsViewModel : BaseViewModel() {

    private val mRepo = ChatRepo()

    var chatID: String? = null
    var astrologerNumber: String? = null

    val orderDetailsLiveData by lazy { MutableLiveData<BaseResponseModel<OrderDetailResponse>>() }
    val chatHistoryResponse by lazy { MutableLiveData<BaseResponseModel<List<MessageResponse>>>() }
    val endChatResponse by lazy { MutableLiveData<BaseResponseModel<Any>>() }
    val userNameForReviewLiveData by lazy { MutableLiveData<String>() }
    val submitReviewLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }

    val connectionLiveData by lazy { ConnectionLiveData(BaseApplication.instance) }


    //socket chat code

    // Create a LiveData with a lazy property
    val stompConnectionLiveData: MutableLiveData<LifecycleEvent> by lazy {
        MutableLiveData<LifecycleEvent>()
    }

    val sendNewMessageLiveData: MutableLiveData<MessageResponse?> by lazy {
        MutableLiveData<MessageResponse?>()
    }

    val replyMessageLiveData: MutableLiveData<ArrayList<MessageResponse>> by lazy {
        MutableLiveData<ArrayList<MessageResponse>>()
    }


    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null
    private val SOCKET_URL = ("${BuildConfig.WEB_SOCKET_BASE_URL}ChatMachine/astrology/websocket")
    private var SERVER_REPLY_END_POINT = "/topic/requests/astrology/${mRepo.getMsisdn()}"
    private var SEND_MESSAGE_END_POINT = "/chatapp/message/send"


    fun getMsisdn() = mRepo.getMsisdn()


    fun getUserName() {
        viewModelScope.launch {
            var name = mRepo.getUserName()
            if (name.isBlank()) {
                val formData = mRepo.getIntakeFormData()
                formData?.let {
                    name = ("${it.firstName} ${it.lastName}").trim()
                }
            }
            if (name.isBlank()) {
                val msisdn = mRepo.getMsisdn()
                name = ("${msisdn.substring(0, 4)}xxxxxx")
            }
            userNameForReviewLiveData.value = name
        }
    }


    fun getChatStartTime(): String {
        var time: String? = mRepo.getChatStartTime()
        if (time.isNullOrBlank()) {
            time = System.currentTimeMillis().toString()
            setChatStartTime(time)
        }
        setUserBusy(true)
        return time
    }

    fun setChatStartTime(time: String?) {
        mRepo.setChatStartTime(time)
    }

    fun setUserBusy(isBusy: Boolean = false) {
        mRepo.setUserBusy(isBusy)
    }


    fun getOrderDetails(orderId: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                mRepo.getOrderDetails(
                    ApiRequestCodes.ORDER_DETAILS,
                    orderId,
                    orderDetailsLiveData,
                    errorLiveData
                )

            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun getChatHistory() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val paramMap = HashMap<String, String>()
                paramMap[ChatApiKeys.appName] = BuildConfig.APP_NAME
                paramMap[ChatApiKeys.chatId] = chatID ?: ""
                paramMap[ChatApiKeys.from] = DataManager.getMsisdn() ?: ""

                mRepo.getChatHistory(
                    ApiRequestCodes.GET_CHAT,
                    paramMap,
                    chatHistoryResponse,
                    errorLiveData
                )

            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun endChat() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val paramMap = HashMap<String, String>()
                paramMap.put(ChatApiKeys.appName, BuildConfig.APP_NAME)
                paramMap.put(ChatApiKeys.from, DataManager.getMsisdn())
                paramMap.put(ChatApiKeys.to, astrologerNumber ?: "")
                paramMap.put(ChatApiKeys.chatId, chatID ?: "")

                mRepo.endChat(
                    ApiRequestCodes.END_CHAT,
                    paramMap,
                    endChatResponse,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun submitReview(id: String, rating: Float, review: String, name: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                mRepo.submitReview(
                    ApiRequestCodes.SUBMIT_REVIEW,
                    id,
                    rating,
                    review,
                    name,
                    submitReviewLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    // web socket code

    fun isSocketConnected(): Boolean {
        return (mStompClient?.isConnected == true)
    }

    fun checkAndConnectSocket() {
        if (navigator?.isNetworkAvailable() == true) {
            if (isSocketConnected()) {
                return
            } else if (mStompClient != null && !mStompClient!!.isConnected) {
                disconnectSocket()
                connectWebSocket()
            } else {
                connectWebSocket()
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    private fun connectWebSocket() {
        compositeDisposable = CompositeDisposable()
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL)
//        mStompClient?.withClientHeartbeat(1000)?.withServerHeartbeat(1000)

        val lifecycle = mStompClient?.lifecycle()?.subscribe { lifecycleEvent ->
            stompConnectionLiveData.postValue(lifecycleEvent)
            when (lifecycleEvent?.type) {
                LifecycleEvent.Type.OPENED -> {
                    subscribeToReplyEndPoint()
                }
                LifecycleEvent.Type.CLOSED -> {
                    disconnectSocket()
                }
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                    disconnectSocket()
                }
                LifecycleEvent.Type.ERROR -> {
                    disconnectSocket()
                }
            }
        }
        if (!mStompClient?.isConnected!!) {
            mStompClient?.connect()
        }
        if (lifecycle != null) {
            compositeDisposable?.add(lifecycle)
        }
    }


    //listening to new message on the socket
    private fun subscribeToReplyEndPoint() {
        val topic = mStompClient?.topic(SERVER_REPLY_END_POINT)?.subscribe({ stompMessage ->
            val replyModel = Gson().fromJson(stompMessage?.payload, MessageResponse::class.java)
            replyModel?.let {
                var list = replyMessageLiveData.value
                if (list == null) {
                    list = ArrayList()
                }
                list.add(it)
                replyMessageLiveData.postValue(list)
            }

        }, { throwable ->
            Log.d("Error ", "subscribing to $SERVER_REPLY_END_POINT")
        })
        compositeDisposable?.add(topic!!)
    }


    //sending new message on socket
    fun sendNewMessage(message: String?, isFirstMessage: Boolean = false) {
        mStompClient?.let { it ->

            val model = SendMessageRequest(
                chatId = chatID ?: "",
                to = astrologerNumber ?: "",
                from = getMsisdn(),
                message = message ?: "",
                isFirstMessage = isFirstMessage
            )

            val jsonMessage = Gson().toJson(model, SendMessageRequest::class.java)

            compositeDisposable?.add(it.send(
                SEND_MESSAGE_END_POINT,
                jsonMessage
            ).compose(applySchedulers()).subscribe({
                val messageModel = MessageResponse(
                    appName = BuildConfig.APP_NAME,
                    chatId = model.chatId,
                    to = model.to,
                    from = model.from,
                    message = model.message,
                    messageTime = System.currentTimeMillis(),
                    userRole = UserRole.ROLE_CLIENT.value,
                    chatEnd = false
                )
                sendNewMessageLiveData.value = messageModel
            }
            ) { throwable: Throwable ->
                sendNewMessageLiveData.value = null  //indicates error in sending message
            })
        }
    }


    private fun applySchedulers(): CompletableTransformer {
        return CompletableTransformer { upstream: Completable ->
            upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }


    fun disconnectSocket() {
        if (mStompClient != null) {
            if (mStompClient!!.isConnected) {
                mStompClient?.disconnect()
            }
        }
        compositeDisposable?.dispose()
    }

    override fun onCleared() {
        disconnectSocket()
        super.onCleared()
    }


}
