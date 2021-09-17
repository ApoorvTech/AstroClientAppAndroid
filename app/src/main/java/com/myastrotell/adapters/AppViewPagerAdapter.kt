package com.myastrotell.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class AppViewPagerAdapter(
    fm: FragmentManager,
    private val fragmentList: ArrayList<Fragment>,
    private val titleList: ArrayList<String>? = null
) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }


    override fun getCount() = fragmentList.size


    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        if (titleList != null)
            title = titleList[position]
        return title
    }

}