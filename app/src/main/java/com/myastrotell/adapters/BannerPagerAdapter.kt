package com.myastrotell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.myastrotell.R
import com.myastrotell.pojo.response.Banner
import kotlinx.android.synthetic.main.list_item_banner.view.*


class BannerPagerAdapter(
    private val mBannerList: ArrayList<Banner>,
    private val clickListener : ((Int) -> Unit)?
) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_banner, container, false)

        val sdvImage = view.sdvImage

        val imageUrl = mBannerList[position].bannerURL
        sdvImage.setImageURI(imageUrl)

        sdvImage.setOnClickListener {
            clickListener?.invoke(position)
        }

        view.tag = position
        container.addView(view)

        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount() = mBannerList.size


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

}