package com.myastrotell.ui.chatrequest

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityChatRequestBinding
import com.myastrotell.ui.chat.ChatActivity
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_chat_request.*


class ChatRequestActivity : BaseActivity<ActivityChatRequestBinding, ChatRequestViewModel>() {

    private var astrologerNumber: String? = ""
    private var astrologerName: String? = ""
    private var astrologerImage: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            astrologerNumber = it.getStringExtra(AppConstants.KEY_NUMBER) ?: ""
            astrologerName = it.getStringExtra(AppConstants.KEY_NAME) ?: ""
            astrologerImage = it.getStringExtra(AppConstants.KEY_IMAGE) ?: ""

            sdvImage.setImageURI(astrologerImage)
            atvAstrologerName.text = astrologerName.toString()
        }
    }


    override fun getLayoutId() = R.layout.activity_chat_request


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<ChatRequestViewModel>()


    override fun initVariables() {

    }


    override fun setObservers() {
        viewModel?.initChatLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.let { data ->
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(AppConstants.KEY_CHAT_ID, data.chatId)
                intent.putExtra(AppConstants.KEY_NUMBER, astrologerNumber)
                intent.putExtra(AppConstants.KEY_TITLE, astrologerName)
                intent.putExtra(AppConstants.KEY_IMAGE, astrologerImage)
                intent.putExtra(AppConstants.KEY_IS_SHOWING_HISTORY, false)
                startActivity(intent)

                finish()
            }

        })


        atvAccept.setOnClickListener {
            viewModel?.initChat(astrologerNumber ?: "")
        }

    }


}