package com.myastrotell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.myastrotell.R
import kotlinx.android.synthetic.main.list_item_product_images.view.*


class ProductImagesPagerAdapter(
    private val mImagesList: ArrayList<String>,
    private val clickListener: ((position: Int) -> Unit)? = null
) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.list_item_product_images, container, false)

        val sdvImage = view.sdvImage

        sdvImage.setImageURI(mImagesList[position] ?: "")

        view.setOnClickListener {
            clickListener?.invoke(position)
        }

        view.tag = position
        container.addView(view)

        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount() = mImagesList.size


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

}