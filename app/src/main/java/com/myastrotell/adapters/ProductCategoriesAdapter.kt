package com.myastrotell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.response.ProductDetail
import kotlinx.android.synthetic.main.list_item_mall_categories.view.*


class ProductCategoriesAdapter(
    private val mList: ArrayList<ProductDetail>,
    private val mClickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<ProductCategoriesAdapter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_mall_categories, parent, false)
        )
    }


    override fun getItemCount() = mList.size


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        mList[position].let { data ->
            holder.sdvImage.setImageURI(data.productImage)
            holder.atvTitle.text = data.productTitle
        }
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sdvImage = itemView.sdvImage!!
        val atvTitle = itemView.atvTitle!!

        init {
            itemView.setOnClickListener { mClickListener?.invoke(adapterPosition) }
        }

    }

}