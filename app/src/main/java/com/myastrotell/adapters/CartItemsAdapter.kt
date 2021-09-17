package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.data.database.entities.CartEntity
import kotlinx.android.synthetic.main.list_item_cart_items.view.*


class CartItemsAdapter(
    private val mCartItemsList: ArrayList<CartEntity>,
    private val onQuantityUpdated: (position: Int) -> Unit,
    private val onDeleteClicked: (position: Int) -> Unit,
    private val onConsultantUpdated: (position: Int, consultantName: String) -> Unit

) : RecyclerView.Adapter<CartItemsAdapter.ItemViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_cart_items, parent, false)
        )
    }


    override fun getItemCount() = mCartItemsList.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        mCartItemsList[position].let {
            holder.sdvImage.setImageURI(it.productThumb)

            holder.atvProductName.text = it.productName
            holder.atvProductDesc.text = it.productDescription
            holder.atvQuantity.text = it.quantity.toString()

            holder.atvProductPrice.text = context?.getString(R.string.currency)
            holder.atvProductPrice.append(String.format("%.2f", it.productPrice?.toDouble() ?: 0.0))

            holder.aetConsultant.setText(it.consultantName ?: "")

            it.total = (it.productPrice?.toDouble() ?: 0.0).times(it.quantity)

        }

    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sdvImage = itemView.sdvImage!!
        val atvProductName = itemView.atvProductName!!
        val atvProductDesc = itemView.atvProductDesc!!
        val atvProductPrice = itemView.atvProductPrice!!
        val atvQuantity = itemView.atvQuantity!!
        val aivMinus = itemView.aivMinus!!
        val aivPlus = itemView.aivPlus!!
        val aivDelete = itemView.aivDelete!!
        val aetConsultant = itemView.aetConsultant!!

        init {
            aivMinus.setOnClickListener {
                var quantity = atvQuantity.text.toString().toInt()
                if (quantity > 1) {
                    quantity--
                    atvQuantity.text = quantity.toString()

                    val data = mCartItemsList[adapterPosition]
                    data.quantity = quantity
                    data.total = (data.productPrice?.toDouble() ?: 0.0).times(quantity)

                    onQuantityUpdated.invoke(adapterPosition)

                }
            }

            aivPlus.setOnClickListener {
                var quantity = atvQuantity.text.toString().toInt()
                if (quantity < 100) {
                    quantity++
                    atvQuantity.text = quantity.toString()

                    val data = mCartItemsList[adapterPosition]
                    data.quantity = quantity
                    data.total = (data.productPrice?.toDouble() ?: 0.0).times(quantity)

                    onQuantityUpdated.invoke(adapterPosition)

                }

            }

            aivDelete.setOnClickListener {
                onDeleteClicked.invoke(adapterPosition)
            }


            aetConsultant.doAfterTextChanged {
                onConsultantUpdated.invoke(adapterPosition, aetConsultant.text?.trim().toString())
            }

        }

    }

}