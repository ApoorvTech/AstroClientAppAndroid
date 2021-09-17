package com.myastrotell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.SideMenuModel
import kotlinx.android.synthetic.main.list_item_side_menu_options.view.*


class SideMenuOptionsAdapter(
    private val mOptionsList: ArrayList<SideMenuModel>,
    private val mClickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<SideMenuOptionsAdapter.OptionsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        return OptionsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_side_menu_options, parent, false)
        )
    }


    override fun getItemCount() = mOptionsList.size


    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {

        holder.atvOption.text = mOptionsList[position].option
        holder.atvOption.setCompoundDrawablesWithIntrinsicBounds(
            mOptionsList[position].icon,
            0,
            R.drawable.ic_arrow_forward_dark,
            0
        )

        holder.viewDivider.visibility = if (position == mOptionsList.size - 1)
            View.GONE
        else
            View.VISIBLE

    }


    inner class OptionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvOption = itemView.atvOption!!
        val viewDivider = itemView.viewDivider!!

        init {
            itemView.setOnClickListener { mClickListener?.invoke(adapterPosition) }
        }

    }

}