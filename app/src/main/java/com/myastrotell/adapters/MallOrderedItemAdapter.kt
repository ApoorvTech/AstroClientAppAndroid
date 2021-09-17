package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.MallOrderedItemModel
import kotlinx.android.synthetic.main.list_item_mall_order_items.view.*


class MallOrderedItemAdapter(
    private val mItemsList: ArrayList<MallOrderedItemModel>
) : RecyclerView.Adapter<MallOrderedItemAdapter.ItemViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_mall_order_items, parent, false)
        )
    }


    override fun getItemCount() = mItemsList.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        mItemsList[position].let {

            holder.sdvImage.setImageURI(it.imageUrl)

            holder.atvProductName.text = it.productName

            holder.atvProductDesc.text = it.productCategory

            holder.atvQuantity.text = ("Qty: ${it.productQuantity}")

            holder.atvProductPrice.text = context?.getString(R.string.currency)
            holder.atvProductPrice.append(String.format("%.2f", it.productPrice ?: 0.0))
            holder.atvProductPrice.append("/piece")

            holder.aetConsultant.text = it.productConsultant ?: ""

        }


    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sdvImage = itemView.sdvImage!!
        val atvProductName = itemView.atvProductName!!
        val atvProductDesc = itemView.atvProductDesc!!
        val atvProductPrice = itemView.atvProductPrice!!
        val atvQuantity = itemView.atvProductQuantity!!
        val aetConsultant = itemView.atvConsultant!!

    }

}