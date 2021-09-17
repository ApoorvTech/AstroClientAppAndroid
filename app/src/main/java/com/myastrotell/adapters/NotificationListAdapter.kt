package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.response.Notifications
import com.myastrotell.utils.AppUtils
import kotlinx.android.synthetic.main.list_item_notification.view.*


class NotificationListAdapter(
    private val mNotificationList: ArrayList<Notifications>
) : RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder>() {


    private var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        context = parent.context
        return NotificationViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_notification, parent, false)
        )
    }


    override fun getItemCount() = mNotificationList.size


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        mNotificationList[position].let { data ->
            holder.atvTitle.text = data.title
            holder.atvMessage.text = data.message
            holder.atvDateTime.text =
                AppUtils.timeStampToDate(data.scheduleDate ?: 0, AppUtils.DF_dd_MMM_yyyy)
        }

    }


    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvTitle = itemView.atvTitle!!
        val atvMessage = itemView.atvMessage!!
        val atvDateTime = itemView.atvDateTime!!

    }

}