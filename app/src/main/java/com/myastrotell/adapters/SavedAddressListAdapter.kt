package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.interfaces.SavedAddressItemClickListener
import com.myastrotell.pojo.response.AddressListResponse
import kotlinx.android.synthetic.main.list_item_saved_addresses.view.*


class SavedAddressListAdapter(
    private val mAddressList: ArrayList<AddressListResponse>,
    private val mClickListener: SavedAddressItemClickListener?
) : RecyclerView.Adapter<SavedAddressListAdapter.ItemViewHolder>() {

    var context: Context? = null
    var selectedPosition: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_saved_addresses, parent, false)
        )
    }


    override fun getItemCount() = mAddressList.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        mAddressList[position].let {

            holder.atvName.text = it.userName.toString()

            val address = StringBuilder()
            address.append(it.houseNo.toString())
            address.append(", ")
            address.append(it.addressLine1.toString())
            if (!it.addressLine2.isNullOrBlank()) {
                address.append(", ")
                address.append(it.addressLine2.toString())
            }
            address.append(", ")
            address.append(it.state.toString())
            address.append(", ")
            address.append(it.city.toString())
            address.append(", ")
            address.append(it.country ?: "India")
            address.append(" - ")
            address.append(it.pinCode.toString())

            holder.atvAddressDetails.text = address


            if (selectedPosition == position) {
                holder.aivSelected.setImageResource(R.drawable.ic_selected_radio_button)
            } else {
                holder.aivSelected.setImageResource(R.drawable.ic_unselected_radio_button)
            }

        }

    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aivSelected = itemView.aivSelected!!
        val atvName = itemView.atvName!!
        val atvAddressDetails = itemView.atvAddressDetails!!
        val aivEdit = itemView.aivEdit!!
        val aivDelete = itemView.aivDelete!!

        init {
            aivSelected.setOnClickListener {
                if (adapterPosition >= 0 && adapterPosition < mAddressList.size) {
                    selectedPosition = adapterPosition
                    notifyDataSetChanged()

                    mClickListener?.onItemSelected(adapterPosition)
                }
            }

            atvName.setOnClickListener { mClickListener?.onItemSelected(adapterPosition) }

            aivEdit.setOnClickListener { mClickListener?.onEditClicked(adapterPosition) }

            aivDelete.setOnClickListener { mClickListener?.onDeleteClicked(adapterPosition) }

        }

    }


}