package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.response.HororsopeDetails
import kotlinx.android.synthetic.main.list_item_daily_horoscope.view.*


class HoroscopeListAdapter(
    private val mHororscopeList: ArrayList<HororsopeDetails>
) : RecyclerView.Adapter<HoroscopeListAdapter.HoroscopeViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        context = parent.context
        return HoroscopeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_daily_horoscope, parent, false)
        )
    }


    override fun getItemCount() = mHororscopeList.size


    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {

        holder.atvTitle.text = mHororscopeList[position].productTitle
        // holder.atvDescription.text = mHororscopeList[position].productDescription

        val text: StringBuilder = java.lang.StringBuilder("<html><body><p align=\"justify\">")
        val textToAppend = "</p></body></html>"

        text.append(mHororscopeList[position].productDescription)
        text.append(textToAppend)

        holder.webView.loadData(text.toString(), "text/html", "utf-8")

    }


    inner class HoroscopeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvTitle = itemView.atvTitle!!
        val atvDescription = itemView.atvDescription!!
        val webView = itemView.webView!!

    }

}