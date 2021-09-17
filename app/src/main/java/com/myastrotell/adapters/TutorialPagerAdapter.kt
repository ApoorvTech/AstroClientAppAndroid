package com.myastrotell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.myastrotell.R
import kotlinx.android.synthetic.main.list_item_tutorial.view.*

class TutorialPagerAdapter(private val datalist: List<Int>) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_tutorial, container, false)

        view.tag = position
        view.tutorialIV.setImageResource(datalist[position])
        container.addView(view)

        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }


    override fun getCount() = datalist.size


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

}