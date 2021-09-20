package com.myastrotell.ui.dailyhoroscope

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.BaseApplication
import com.myastrotell.R
import com.myastrotell.adapters.HoroscopeListAdapter
import com.myastrotell.adapters.SunSignListAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.data.AstrologersListType
import com.myastrotell.data.CapturedEvents
import com.myastrotell.data.OrderHistoryType
import com.myastrotell.databinding.ActivityDailyHoroscopeBinding
import com.myastrotell.pojo.response.Feature
import com.myastrotell.pojo.response.HororsopeDetails
import com.myastrotell.ui.astrologerslist.AstrologersListActivity
import com.myastrotell.ui.astrologynews.AstrologyNewsActivity
import com.myastrotell.ui.home.readstory.ReadStoryActivity
import com.myastrotell.ui.home.watchstory.WatchStoryActivity
import com.myastrotell.ui.login.LoginSignUpActivity
import com.myastrotell.ui.notifications.NotificationsActivity
import com.myastrotell.ui.orderhistory.OrderHistoryActivity
import com.myastrotell.ui.support.SupportActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_daily_horoscope.*
import kotlinx.android.synthetic.main.activity_daily_horoscope.rvSunSigns
import kotlinx.android.synthetic.main.layout_home.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class DailyHoroscopeActivity :
    BaseActivity<ActivityDailyHoroscopeBinding, DailyHororscopeViewModel>(),
    View.OnClickListener {

    private lateinit var mSunSignAdapter: SunSignListAdapter
    private lateinit var mSunSignList: ArrayList<Feature>

    private lateinit var mHoroscopeAdapter: HoroscopeListAdapter
    private lateinit var mHoroscopeList: ArrayList<HororsopeDetails>

    private lateinit var mHoroscopeMap: HashMap<String?, List<HororsopeDetails>>

    private var selectedPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rlgochat.setOnClickListener(this)
        getData()

        setUpAdapters()

        getSelectedHoroscope()

    }


    override fun getLayoutId() = R.layout.activity_daily_horoscope


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<DailyHororscopeViewModel>()


    override fun initVariables() {
        mSunSignList = ArrayList()
        mHoroscopeList = ArrayList()
        mHoroscopeMap = HashMap()
    }


    private fun getData() {
        selectedPosition = intent?.getIntExtra(AppConstants.KEY_POSITION, 0)!!
        val list = intent?.getParcelableArrayListExtra<Feature>(AppConstants.KEY_DATA)

        mSunSignList.clear()
        list?.let {
            mSunSignList.addAll(it)
        }

        if (selectedPosition < mSunSignList.size) {
            mSunSignList[selectedPosition].isSelected = true
        }

        setToolbarTitle()

    }


    override fun setObservers() {
        viewModel?.horoscopeLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.let { horoscope ->

                val sunSign = mSunSignList[selectedPosition].featureName

                if (!mHoroscopeMap.containsKey(sunSign)) {

                    mHoroscopeList.clear()

                    horoscope.productDetails?.let { horoscopeData ->
                        mHoroscopeMap[sunSign] = horoscopeData
                        mHoroscopeList.addAll(horoscopeData)
                    }

                    mHoroscopeAdapter.notifyDataSetChanged()

                    if (mHoroscopeList.isNotEmpty()) {
                        rvHoroscope.post {
                            rvHoroscope.scrollToPosition(0)
                        }
                    }
                }
            }
        })


        aivBack.setOnClickListener(this)

    }


    /**
     * method to set toolbar title
     */
    private fun setToolbarTitle() {
        if (selectedPosition < mSunSignList.size) {
            atvTitle.text =
                ("${getString(R.string.daily_horoscope)} (${mSunSignList[selectedPosition].featureName})")
        }
    }


    /**
     * method to set up Adapters
     */
    private fun setUpAdapters() {
        // setting Sun-Sign list adapter
        mSunSignAdapter = SunSignListAdapter(mSunSignList, mClickListener = { position ->
            if(selectedPosition != position) {
                mSunSignList[selectedPosition].isSelected = false

                selectedPosition = position
                mSunSignList[selectedPosition].isSelected = true
                mSunSignAdapter.notifyDataSetChanged()

                setToolbarTitle()

                scrollToSelectedItem()

                getSelectedHoroscope()
            }
        }, showSelection = true)

        rvSunSigns.itemAnimator = DefaultItemAnimator()
        rvSunSigns.adapter = mSunSignAdapter
        scrollToSelectedItem()


        // setting Horoscope list adapter
        mHoroscopeAdapter = HoroscopeListAdapter(mHoroscopeList)
        rvHoroscope.itemAnimator = DefaultItemAnimator()
        rvHoroscope.adapter = mHoroscopeAdapter

    }


    // scrolling sunSign recyclerView to selected item
    private fun scrollToSelectedItem() {
        rvSunSigns.post {
            rvSunSigns.smoothScrollToPosition(selectedPosition)
        }
    }


    /**
     * method to get selected horoscope
     */
    private fun getSelectedHoroscope() {
        if (selectedPosition < mSunSignList.size) {
            val sunSign = mSunSignList[selectedPosition].featureName

            if (mHoroscopeMap.containsKey(sunSign)) {

                mHoroscopeList.clear()
                mHoroscopeList.addAll(mHoroscopeMap[sunSign]!!)
                mHoroscopeAdapter.notifyDataSetChanged()

                rvHoroscope.post {
                    rvHoroscope.scrollToPosition(0)
                }

            } else {
                viewModel?.getHoroscope(sunSign)
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }
            R.id.rlgochat -> {
                val intent = Intent(this, AstrologersListActivity::class.java)
                intent.putExtra(AppConstants.KEY_TYPE, AstrologersListType.CALL.value)
                startActivity(intent)
            }
        }
    }


    override fun showProgressBar() {
        progressBar?.visible()
    }


    override fun hideProgressBar() {
        progressBar?.gone()
    }

}
