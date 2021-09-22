package com.myastrotell.ui.astrologynews

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.adapters.AppViewPagerAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.databinding.ActivityAstrologyNewsBinding
import com.myastrotell.pojo.response.ProductDetail
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_astrology_news.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class AstrologyNewsActivity : BaseActivity<ActivityAstrologyNewsBinding, AstrologyNewsViewModel>(),
    View.OnClickListener {

    private lateinit var mPagerAdapter: AppViewPagerAdapter
    private lateinit var mFragmentList: ArrayList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.astrology_news)

        viewModel?.getNews()
    }


    override fun getLayoutId() = R.layout.activity_astrology_news

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<AstrologyNewsViewModel>()

    override fun initVariables() {

    }


    override fun setObservers() {

        viewModel?.newsLiveData?.observe(this, Observer {

            hideProgressBar()
            it.data?.productDetails?.let { newsList ->
                Log.d("gsgsg",newsList.toString())
                setUpPagerAdapter(newsList)
            }
        }
        )

        aivBack.setOnClickListener(this)
    }


    private fun setUpPagerAdapter(newsList: List<ProductDetail>) {
             mFragmentList = ArrayList()
        val titleList = ArrayList<String>()

        for(data in newsList){
            val fragment = AstroNewsFragment()
            fragment.news = data

            mFragmentList.add(fragment)
            titleList.add(data.productTitle)


        }
        Log.d("TAGlist", mFragmentList.toString())
        mPagerAdapter = AppViewPagerAdapter(supportFragmentManager, mFragmentList, titleList)
        vpAstrologyNews.adapter = mPagerAdapter

        tabLayout.setupWithViewPager(vpAstrologyNews)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }
        }
    }


}