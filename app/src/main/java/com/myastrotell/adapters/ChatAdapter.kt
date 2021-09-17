package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.pojo.response.MessageResponse
import com.myastrotell.utils.AppUtils
import kotlinx.android.synthetic.main.layout_submit_reviews.view.*
import kotlinx.android.synthetic.main.list_item_my_message.view.*


class ChatAdapter(
    private val mMessageList: ArrayList<MessageResponse>,
    private val ratingSubmitListener: ((rating: Float, review: String) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var context: Context? = null
    var myMsisdnNumber: String = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            ApiStatusCodes.CHAT_END -> {
                ReviewViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_submit_reviews, parent, false)
                )
            }
            0 -> {
                MyMessageViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.list_item_my_message, parent, false)
                )
            }
            else -> {
                OthersMessageViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.list_item_other_message, parent, false)
                )
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val data = mMessageList[position]

        return when {
            data.chatEnd == true -> {
                ApiStatusCodes.CHAT_END
            }
            data.from.equals(myMsisdnNumber, true) -> {
                0
            }
            else -> {
                1
            }
        }
    }


    override fun getItemCount() = mMessageList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val data = mMessageList[position]

        when (getItemViewType(position)) {
            ApiStatusCodes.CHAT_END -> {

            }

            0 -> {
                val myMessageHolder = holder as MyMessageViewHolder
                myMessageHolder.atvMessage.text = data.message
                myMessageHolder.atvTime.text =
                    AppUtils.timeStampToDate(data.messageTime, AppUtils.DF_dd_MMM_yy_hh_mm_aa)
            }

            else -> {
                val otherMessageHolder = holder as OthersMessageViewHolder
                otherMessageHolder.atvMessage.text = data.message
                otherMessageHolder.atvTime.text =
                    AppUtils.timeStampToDate(data.messageTime, AppUtils.DF_dd_MMM_yy_hh_mm_aa)
            }

        }

    }


    inner class MyMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvMessage = itemView.atvMessage!!
        val atvTime = itemView.atvTime!!

    }

    inner class OthersMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvMessage = itemView.atvMessage!!
        val atvTime = itemView.atvTime!!

    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aetReview = itemView.aetReview!!
        val reviewRatingBar = itemView.reviewRatingBar!!
        val atvReviewTextCount = itemView.atvReviewTextCount!!
        val atvSubmit = itemView.atvSubmit!!

        init {
            atvSubmit.setOnClickListener {
                ratingSubmitListener?.invoke(reviewRatingBar.rating, aetReview.text.toString())
            }

            aetReview.doAfterTextChanged {
                atvReviewTextCount.text = ("${it.toString().length}/160")
            }

//            aetReview.filters = AppUtils.getOmitEmojiFilter()

        }

    }


}