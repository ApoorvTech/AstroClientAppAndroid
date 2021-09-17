package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.interfaces.ProductItemClickListener
import com.myastrotell.pojo.response.RedemptionSearchResponse
import kotlinx.android.synthetic.main.list_item_mall_items.view.*


class MallItemsListAdapter(
    private val mItemsList: ArrayList<RedemptionSearchResponse>,
    private val mClickListener: ProductItemClickListener?,
    private val onSearchFinished: (Int) -> Unit
) : RecyclerView.Adapter<MallItemsListAdapter.ItemViewHolder>(), Filterable {

    var context: Context? = null
    private val mList = ArrayList<RedemptionSearchResponse>()
    private var mFilter: MallItemsListAdapter.MyFilter? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_mall_items, parent, false)
        )
    }


    override fun getItemCount() = mItemsList.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        mItemsList[position].let { data ->

            holder.sdvImage.setImageURI(data.goodsImage)

            holder.atvProductName.text = data.goodsName

            holder.atvProductDetails.text = data.goodsDescription

            holder.atvProductPrice.text =
                (context?.getString(R.string.currency) + "${data.goodsPrice}/piece")

            holder.atvAddToCart.visibility = if (data.isAddedToCart)
                View.INVISIBLE
            else
                View.VISIBLE

        }

    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sdvImage = itemView.sdvImage!!
        val atvProductName = itemView.atvProductName!!
        val atvProductDetails = itemView.atvProductDetails!!
        val atvProductPrice = itemView.atvProductPrice!!
        val atvAddToCart = itemView.atvAddToCart!!

        init {
            atvAddToCart.setOnClickListener { mClickListener?.onAddToCartClicked(adapterPosition) }

            itemView.setOnClickListener { mClickListener?.onItemClicked(adapterPosition) }

        }

    }


    fun updateList(list: ArrayList<RedemptionSearchResponse>?) {
        list?.let {
            mItemsList.clear()
            mList.clear()

            mItemsList.addAll(it)
            mList.addAll(it)
            notifyDataSetChanged()
        }
    }


    override fun getFilter(): Filter {
        if (mFilter == null)
            mFilter = MyFilter()

        return mFilter!!
    }


    private inner class MyFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val query = constraint?.trim().toString().toLowerCase()

            val resultList = ArrayList<RedemptionSearchResponse>()

            if (query.isBlank()) {
                resultList.addAll(mList)

            } else {
                mList.forEach {
                    if (it.goodsName.toString().toLowerCase().contains(query)) {
                        resultList.add(it)
                    }
                }
            }

            val filterResult = FilterResults()
            filterResult.values = resultList
            filterResult.count = resultList.size

            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            mItemsList.clear()
            mItemsList.addAll(results?.values as ArrayList<RedemptionSearchResponse>)
            notifyDataSetChanged()

            onSearchFinished.invoke(mItemsList.size)
        }

    }


}