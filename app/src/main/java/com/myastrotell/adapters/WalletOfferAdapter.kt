package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.response.OfferListResponse
import kotlinx.android.synthetic.main.list_item_wallet_offers.view.*


class WalletOfferAdapter(
    private val mAmountList: ArrayList<OfferListResponse>,
    private val mClickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<WalletOfferAdapter.OfferViewHolder>() {


    private var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        context = parent.context
        return OfferViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_wallet_offers, parent, false)
        )
    }


    override fun getItemCount() = mAmountList.size


    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        mAmountList[position].let {
            holder.atvAmount.text = ("₹${(it.cardAmount ?: 0).toInt()}")
            holder.atvOfferStrip.text = ("${(it.discountPercent ?: 0).toInt()}% OFF")

            if (it.isSelected){
                holder.flCard.setBackgroundResource(R.drawable.shape_rect_green_corners_8dp)
            } else{
                holder.flCard.setBackgroundResource(R.drawable.shape_rect_stroke_light_gray_corners_8dp)
            }
        }
    }


    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvAmount = itemView.atvAmount!!
        val atvOfferStrip = itemView.atvOfferStrip!!
        val flCard = itemView.flCard!!

        init {
            itemView.setOnClickListener { mClickListener?.invoke(adapterPosition) }
        }

    }

}