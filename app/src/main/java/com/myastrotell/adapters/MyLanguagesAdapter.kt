package com.myastrotell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.interfaces.MyLanguageItemClickListener
import com.myastrotell.pojo.response.KeyDataResponse
import kotlinx.android.synthetic.main.list_item_my_languages.view.*

class MyLanguagesAdapter(
    val mDatalist: ArrayList<KeyDataResponse>,
    private val mClickListener: MyLanguageItemClickListener?
) : RecyclerView.Adapter<MyLanguagesAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_my_languages, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.atvLanguage.text = mDatalist[position].value

        if (mDatalist[position].isSelected)
            holder.aivCheck.setImageResource(R.drawable.ic_checked_primary_circular)
        else
            holder.aivCheck.setImageResource(R.drawable.ic_unselected_radio_button)
    }

    override fun getItemCount(): Int {
        return mDatalist.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvLanguage = itemView.atvLanguage!!
        val aivCheck = itemView.aivCheck!!
        val myLanguagesRootView = itemView.myLanguagesRootView!!

        init {
            myLanguagesRootView.setOnClickListener { mClickListener?.onItemCheck(adapterPosition) }

        }

    }


}