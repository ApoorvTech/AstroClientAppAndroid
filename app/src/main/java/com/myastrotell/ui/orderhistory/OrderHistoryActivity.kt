package com.myastrotell.ui.orderhistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.myastrotell.R
import com.myastrotell.adapters.AppViewPagerAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.AppConstants
import com.myastrotell.data.OrderHistoryType
import com.myastrotell.databinding.ActivityOrderHistoryBinding
import com.myastrotell.utils.gone
import kotlinx.android.synthetic.main.activity_order_history.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class OrderHistoryActivity : BaseActivity<ActivityOrderHistoryBinding, BaseViewModel>(),
    View.OnClickListener {

    private lateinit var mPagerAdapter: AppViewPagerAdapter
    private lateinit var mFragmentList: ArrayList<Fragment>
    private lateinit var mTitleList: ArrayList<String>

    private var type = OrderHistoryType.ALL.value


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        setupPagerAdapter()

    }


    override fun getLayoutId() = R.layout.activity_order_history

    override fun getBindingVariable() = 0

    override fun initViewModel() = null

    override fun initVariables() {
        mFragmentList = ArrayList()
        mTitleList = ArrayList()
    }


    private fun getData() {
        atvTitle.text = getString(R.string.order_history)

        type = intent.getStringExtra(AppConstants.KEY_TYPE) ?: OrderHistoryType.ALL.value

        when (type) {
            OrderHistoryType.CHAT.value, OrderHistoryType.CALL.value,
            OrderHistoryType.REPORT.value, OrderHistoryType.MALL.value -> {

                flTabs.gone()

                val bundle = Bundle()
                bundle.putString(AppConstants.KEY_TYPE, type)

                val fragment = OrderHistoryFragment()
                fragment.arguments = bundle

                mFragmentList.add(fragment)
                mTitleList.add("")

            }

            else -> {
                // adding All fragments

                // adding CHAT fragment
                var bundle = Bundle()
                bundle.putString(AppConstants.KEY_TYPE, OrderHistoryType.CHAT.value)

                var fragment = OrderHistoryFragment()
                fragment.arguments = bundle

                mFragmentList.add(fragment)
                mTitleList.add(getString(R.string.chat))


                // adding CALL fragment
                bundle = Bundle()
                bundle.putString(AppConstants.KEY_TYPE, OrderHistoryType.CALL.value)

                fragment = OrderHistoryFragment()
                fragment.arguments = bundle

                mFragmentList.add(fragment)
                mTitleList.add(getString(R.string.call))


                // adding REPORT fragment
                bundle = Bundle()
                bundle.putString(AppConstants.KEY_TYPE, OrderHistoryType.REPORT.value)

                fragment = OrderHistoryFragment()
                fragment.arguments = bundle

                mFragmentList.add(fragment)
                mTitleList.add(getString(R.string.report))


                // adding ASTRO MALL fragment
                bundle = Bundle()
                bundle.putString(AppConstants.KEY_TYPE, OrderHistoryType.MALL.value)

                fragment = OrderHistoryFragment()
                fragment.arguments = bundle

                mFragmentList.add(fragment)
                mTitleList.add(getString(R.string.astro_mall))
            }
        }

    }


    override fun setObservers() {

        aivBack.setOnClickListener(this)
    }


    private fun setupPagerAdapter() {
        mPagerAdapter = AppViewPagerAdapter(supportFragmentManager, mFragmentList, mTitleList)
        vpOrderHistroy.adapter = mPagerAdapter
        tabLayout.setupWithViewPager(vpOrderHistroy)
        vpOrderHistroy.offscreenPageLimit = mFragmentList.size - 1
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }
        }
    }

}