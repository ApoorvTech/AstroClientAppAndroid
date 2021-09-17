package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.interfaces.DetailFormSelection
import com.myastrotell.pojo.DetailFormSelectionModel
import kotlinx.android.synthetic.main.item_detailform_selection.view.*

class DetailFormSelectionAdapter(
    val context: Context,
    val datalist: ArrayList<DetailFormSelectionModel>,
    val listener: DetailFormSelection
) : RecyclerView.Adapter<DetailFormSelectionAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detailform_selection, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = datalist.get(position)
        holder.atvTitle.text = data.title
        if (data.isSelected) {
            if (position == datalist.size - 1) {
                holder.atvTitle.background =
                    context.resources.getDrawable(R.drawable.drawable_detailform_selected_item)
            } else {
                holder.atvTitle.setBackgroundColor(context.resources.getColor(R.color.colorBlackOpaque8))
            }
        } else {
            if (position == datalist.size - 1) {
                holder.atvTitle.background =
                    context.resources.getDrawable(R.drawable.drawable_detailform_unselected_item)
            } else {
                holder.atvTitle.setBackgroundColor(context.resources.getColor(R.color.colorBlackOpaque4))
            }
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvTitle = itemView.atvTitle

        init {
            atvTitle.setOnClickListener { listener?.onItemSelection(adapterPosition) }
        }
    }


}