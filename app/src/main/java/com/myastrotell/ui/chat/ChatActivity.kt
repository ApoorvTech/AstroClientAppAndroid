package com.myastrotell.ui.chat

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.BuildConfig
import com.myastrotell.R
import com.myastrotell.adapters.ChatAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.DataManager
import com.myastrotell.databinding.ActivityChatBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.pojo.response.MessageResponse
import com.myastrotell.utils.MarsApiStatus
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import kotlinx.android.synthetic.main.activity_chat.*
import ua.naiksoftware.stomp.dto.LifecycleEvent


class ChatActivity : BaseActivity<ActivityChatBinding, ChatsViewModel>(), View.OnClickListener {

    private lateinit var mChatAdapter: ChatAdapter
    private lateinit var mMessageList: ArrayList<MessageResponse>

    private var lastVisibleMessagePosition: Int = 0

    private lateinit var orderId: String
    private var isShowingHistory = false
    private var showReviewForOrder = false
    private var isInitiatingChat = false
    private var isRegisteredForConnection = false

    private var providedRating: Float = 0f
    private var providedReview: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        setUpAdapter()

        if (isShowingHistory) {
            viewModel?.getOrderDetails(orderId)

        } else {
            if (!isInitiatingChat) {
                viewModel?.getChatHistory()
            }
            viewModel?.checkAndConnectSocket()
            setInternetConnectionObserver()
        }
    }


    override fun onDestroy() {
        viewModel?.connectionLiveData?.removeObservers(this)
        viewModel?.disconnectSocket()
        super.onDestroy()
    }


    override fun getLayoutId() = R.layout.activity_chat


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<ChatsViewModel>()


    override fun initVariables() {
        mMessageList = ArrayList()
    }


    private fun getData() {
        atvTitle.text = intent.getStringExtra(AppConstants.KEY_TITLE) ?: ""
        sdvImage.setImageURI(intent.getStringExtra(AppConstants.KEY_IMAGE))

        orderId = intent?.getStringExtra(AppConstants.KEY_ID) ?: ""

        viewModel?.chatID = intent?.getStringExtra(AppConstants.KEY_CHAT_ID) ?: ""
        viewModel?.astrologerNumber = intent?.getStringExtra(AppConstants.KEY_NUMBER) ?: ""

        isShowingHistory =
            intent?.getBooleanExtra(AppConstants.KEY_IS_SHOWING_HISTORY, false) ?: false

        isInitiatingChat =
            intent?.getBooleanExtra(AppConstants.KEY_IS_INITIATING_CHAT, false) ?: false


        if (isShowingHistory) {
            chronometerChatTimeElapsed.gone()
            llOptions.gone()
            rlBottom.gone()

            val orderTimeStamp = intent?.getLongExtra(AppConstants.KEY_DATE, 0) ?: 0

            val dateDiff = (System.currentTimeMillis() - orderTimeStamp) / (1000 * 3600 * 24)

            if (dateDiff < 4) {
                showReviewForOrder = true
            }

        } else {
            var min = 0L
            var sec = 0L

            val startTime = viewModel?.getChatStartTime().toString().toLong()
            val currentTime = System.currentTimeMillis()

            val diff = currentTime - startTime

            min = getMinutesFromLong(diff)
            sec = getSecondsFromLong(diff)

            chronometerChatTimeElapsed.base =
                SystemClock.elapsedRealtime() - (min * 60000 + sec * 1000)

            chronometerChatTimeElapsed.start()
            chronometerChatTimeElapsed.format = "Chat in progress (%s mins)"

        }

    }


    override fun setObservers() {

        viewModel?.orderDetailsLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.deliveryAddress?.let { value ->

                if (value.contains("|")) {

                    val list = value.split("|")

                    viewModel?.chatID = list[list.size - 1]

                    viewModel?.getChatHistory()

                }
            }

        })


        viewModel?.chatHistoryResponse?.observe(this, Observer {
            hideProgressBar()

            it.data?.let { messageList ->

                mMessageList.addAll(0, messageList)
                mChatAdapter.notifyDataSetChanged()

                if (showReviewForOrder) {
                    showSubmitReview()

                } else {
                    scrollChatListToBottom()
                }
            }

        })


        viewModel?.stompConnectionLiveData?.observe(this, Observer {
            hideProgressBar()
            if (it.type == LifecycleEvent.Type.OPENED) {
                if (isInitiatingChat) {
                    val initialMessage = intent?.getStringExtra(AppConstants.KEY_MESSAGE)
                    viewModel?.sendNewMessage(initialMessage, true)
                }
            }
        })


        viewModel?.sendNewMessageLiveData?.observe(this, Observer {
            hideProgressBar()
            it?.let { model ->
                isInitiatingChat = false
                mMessageList.add(model)
                mChatAdapter.notifyDataSetChanged()
                scrollChatListToBottom()
            }
        })


        viewModel?.replyMessageLiveData?.observe(this, Observer { newMessageList ->
            newMessageList?.let { list ->
                list.forEach { model ->
                    if (model.chatEnd == true) {
                        viewModel?.disconnectSocket()
                        viewModel?.connectionLiveData?.removeObservers(this)
                        showSubmitReview()

                    } else if (model.to.equals(viewModel?.getMsisdn(), true)) {
                        mMessageList.add(model)
                        mChatAdapter.notifyDataSetChanged()
                        scrollChatListToBottom()
                    }
                }
                viewModel?.replyMessageLiveData?.value?.clear()
            }
        })


        viewModel?.endChatResponse?.observe(this, Observer {
            hideProgressBar()
            viewModel?.disconnectSocket()
            viewModel?.connectionLiveData?.removeObservers(this)
            showSubmitReview()
        })


        viewModel?.userNameForReviewLiveData?.observe(this, Observer { userName ->
            viewModel?.submitReview(
                viewModel?.astrologerNumber ?: "",
                providedRating,
                providedReview.trim(),
                userName
            )
        })


        viewModel?.submitReviewLiveData?.observe(this, Observer {
            hideProgressBar()

            val position = mMessageList.size - 1
            mMessageList.removeAt(position)
            mChatAdapter.notifyItemRemoved(position)

        })


        aivBack.setOnClickListener(this)
        atvEndChat.setOnClickListener(this)
        aivSend.setOnClickListener(this)


        rvChatMessages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val mLinearLayoutManager = rvChatMessages.layoutManager as? LinearLayoutManager
                mLinearLayoutManager?.let {
                    lastVisibleMessagePosition = it.findLastVisibleItemPosition()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        rvChatMessages.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                if (mMessageList.size > 1) {
                    rvChatMessages.post {
                        rvChatMessages.scrollToPosition(lastVisibleMessagePosition)
                    }
                }
            }
        })

    }


    private fun setInternetConnectionObserver() {
        viewModel?.connectionLiveData?.observe(this, Observer {
            if (isRegisteredForConnection) {
                when (it.type) {
                    MarsApiStatus.WIFI.ordinal -> {
                        viewModel?.checkAndConnectSocket()
                    }
                    MarsApiStatus.MOBILE.ordinal -> {
                        viewModel?.checkAndConnectSocket()
                    }
                    MarsApiStatus.NONE.ordinal -> {
                        showNoNetworkError()
                    }
                }
            } else {
                isRegisteredForConnection = true
            }
        })
    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        when (responseModel?.code) {

            ApiStatusCodes.CHAT_END -> {
                if (responseModel.apiRequestCode == ApiRequestCodes.NEW_CHATS) {

                    showShortToast(getString(R.string.your_chat_has_ended))

                    showSubmitReview()

                } else {
                    super.handleApiErrorResponse(responseModel)
                }
            }

            else -> {
                super.handleApiErrorResponse(responseModel)
            }
        }
    }


    private fun setUpAdapter() {
        mChatAdapter = ChatAdapter(mMessageList, ratingSubmitListener = { rating, review ->

            if (rating > 0) {
                providedRating = rating
                providedReview = review
                viewModel?.getUserName()

            } else {
                showShortToast("Please provide rating")
            }

        })

        mChatAdapter.myMsisdnNumber = DataManager.getMsisdn()

        rvChatMessages.itemAnimator = DefaultItemAnimator()
        rvChatMessages.adapter = mChatAdapter

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvEndChat -> {
                AppAlertDialog(this,
                    R.drawable.ic_alert,
                    getString(R.string.end_chat),
                    "${getString(R.string.sure_to_end_chat_with)} ${atvTitle.text}?",
                    getString(R.string.confirm),
                    getString(R.string.cancel),
                    onPositiveBtnClicked = {
                        viewModel?.endChat()
                    }
                ).show()
            }

            R.id.aivSend -> {
                if (isNetworkAvailable()) {
                    if (viewModel?.isSocketConnected() == true) {

                        if (aetInput.text.toString().isBlank()) {
                            showShortToast("Please enter message")
                            return
                        }

                        viewModel?.sendNewMessage(aetInput.text.toString().trim())

                        aetInput.setText("")
                    }
                } else {
                    showNoNetworkError()
                }
            }
        }
    }


    private fun showSubmitReview() {
        if (!isFinishing && !isDestroyed) {

            var isReviewShown = false

            viewModel?.setChatStartTime("")
            viewModel?.setUserBusy(false)

            if (mMessageList.isNotEmpty()) {
                isReviewShown = mMessageList[mMessageList.size - 1].chatEnd == true
            }

            if (!isReviewShown) {

                chronometerChatTimeElapsed.stop()
                chronometerChatTimeElapsed.gone()

                hideSoftKeyboard()
                llOptions.gone()
                rlBottom.gone()

                val endChatModel = MessageResponse(
                    BuildConfig.APP_NAME,
                    "",
                    "",
                    "",
                    "",
                    0,
                    "",
                    true
                )
                mMessageList.add(endChatModel)

                mChatAdapter.notifyDataSetChanged()

                scrollChatListToBottom()
            }
        }
    }


    private fun scrollChatListToBottom() {
        if (mMessageList.size > 1) {
            rvChatMessages.post {
                rvChatMessages.scrollToPosition(mMessageList.size - 1)
                lastVisibleMessagePosition = mMessageList.size - 1
            }
        }

    }


    private fun getMinutesFromLong(diff: Long): Long {
        var mins = 0L
        mins = (diff / (1000 * 60))
        return mins
    }


    private fun getSecondsFromLong(diff: Long): Long {
        var seconds = 0L
        seconds = (diff / 1000)
        return seconds
    }


}