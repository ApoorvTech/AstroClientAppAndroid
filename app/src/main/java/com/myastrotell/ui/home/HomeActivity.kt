package com.myastrotell.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.myastrotell.BaseApplication
import com.myastrotell.BuildConfig
import com.myastrotell.R
import com.myastrotell.adapters.BannerPagerAdapter
import com.myastrotell.adapters.SideMenuOptionsAdapter
import com.myastrotell.adapters.SunSignListAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.*
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.databinding.ActivityHomeBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.pojo.SideMenuModel
import com.myastrotell.pojo.response.Banner
import com.myastrotell.pojo.response.Feature
import com.myastrotell.pojo.response.userchatstatus.ChatStatusResposne
import com.myastrotell.ui.astrologerslist.AstrologersListActivity
import com.myastrotell.ui.astrologynews.AstrologyNewsActivity
import com.myastrotell.ui.astromall.categories.AstroMallCategoriesActivity
import com.myastrotell.ui.chat.ChatActivity
import com.myastrotell.ui.dailyhoroscope.DailyHoroscopeActivity
import com.myastrotell.ui.home.readstory.ReadStoryActivity
import com.myastrotell.ui.home.watchstory.WatchStoryActivity
import com.myastrotell.ui.login.LoginSignUpActivity
import com.myastrotell.ui.mylanguages.MyLanguagesActivity
import com.myastrotell.ui.notifications.NotificationsActivity
import com.myastrotell.ui.orderhistory.OrderHistoryActivity
import com.myastrotell.ui.profile.UserProfileActivity
import com.myastrotell.ui.support.SupportActivity
import com.myastrotell.ui.wallet.WalletActivity
import com.myastrotell.ui.wallet.transationhistory.TransactionHistoryActivity
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_home.*


enum class BannerRedirect(val value: String) {
    RECHARGE("Recharge"),
    CHAT("Chat"),
    TALK("Talk"),
    REPORT("Report"),
    MALL("Mall")
}


class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), View.OnClickListener {

    private lateinit var mBannersAdapter: BannerPagerAdapter
    private lateinit var mBannerList: ArrayList<Banner>

    private lateinit var mSunSignAdapter: SunSignListAdapter
    private lateinit var mSunSignList: ArrayList<Feature>

    private var activeChatData: ChatStatusResposne? = null


    private val pushNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            handleAppIntent(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpNavigationDrawer()

        setUpAdapters()

        setListeners()

        viewModel?.getHomeData()

        handleAppIntent(intent)

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(pushNotificationReceiver, IntentFilter(AppConstants.FCM_FILTER_NAME))

    }


    override fun onResume() {
        super.onResume()

        activeChatData = null
        setChatWithAstrologerStatus()

        // setting side menu details
        setUserDetailsOnSideMenu()

        if (isUserLoggedIn()) {
            viewModel?.getWalletBalance()
            viewModel?.getChatStatusForUser()
        }
    }


    private fun setUserDetailsOnSideMenu() {
        if (isUserLoggedIn()) {
            sdvImage.visible()
            sdvImage.setImageURI(viewModel?.getProfileImage())

            val name = ("${viewModel?.getFirstName()} ${viewModel?.getLastName()}")
            if (name.trim().isNotBlank()) {
                atvUserName.text = name
                atvUserName.visible()
            } else {
                atvUserName.gone()
            }

            atvNumber.text = ("+")
            atvNumber.append(getString(R.string.country_code))
            atvNumber.append("-")
            atvNumber.append(viewModel?.getMsisdn())

            aivArrow.visible()

        } else {
            sdvImage.gone()
            atvUserName.gone()
            aivArrow.gone()
            atvNumber.text = getString(R.string.login)
        }
    }


    override fun getLayoutId() = R.layout.activity_home


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<HomeViewModel>()


    override fun initVariables() {
        mBannerList = ArrayList()
        mSunSignList = ArrayList()
    }


    override fun setObservers() {
        viewModel?.homeFeaturesLiveData?.observe(this, Observer {
            hideProgressBar()

            clMain.visible()

            mBannerList.clear()
            mSunSignList.clear()

            it.data?.let { data ->

                data.bannerList?.let { list ->
                    mBannerList.addAll(list)
                    mBannersAdapter.notifyDataSetChanged()
                    ciBanners.setViewPager(vpBanners)
                }

                data.featureList?.let { list ->
                    mSunSignList.addAll(list)
                    mSunSignAdapter.notifyDataSetChanged()
                }

            }

            if (isUserLoggedIn()) {
                viewModel?.getProfileDetails()
            }

            registerFcmId()

        })


        viewModel?.homeStaticDataLiveData?.observe(this, Observer {
            // setting icons and texts from api onn home screen
            it.data?.forEach { data ->
                when (data.id) {
                    // id will be fixed for each element
                    1 -> {
                        aivChat.setImageURI(data.imageUrl.toString())
                        atvLabelChat.text = data.title
                        atvLabelChatRate.text = data.description
                    }
                    2 -> {
                        aivCall.setImageURI(data.imageUrl.toString())
                        atvLabelCall.text = data.title
                        atvLabelCallRate.text = data.description
                    }
                    3 -> {
                        aivReport.setImageURI(data.imageUrl.toString())
                        atvLabelReport.text = data.title
                        atvLabelReportRate.text = data.description
                    }
                    4 -> {
                        aivMall.setImageURI(data.imageUrl.toString())
                        atvLabelMall.text = data.title
                        atvLabelMallRate.text = data.description
                    }
                    5 -> {
                        aivAstroNews.setImageURI(data.imageUrl.toString())
                        atvLabelAstroNews.text = data.title
                        atvLabelNewsDescription.text = data.description
                    }
                    6 -> {
                        aivChatHistory.setImageURI(data.imageUrl.toString())
                        atvChatHistory.text = data.title
                    }
                    7 -> {
                        aivCallHistory.setImageURI(data.imageUrl.toString())
                        atvCallHistory.text = data.title
                    }
                    8 -> {
                        aivReportHistory.setImageURI(data.imageUrl.toString())
                        atvReportHistory.text = data.title
                    }
                    9 -> {
                        aivMallHistory.setImageURI(data.imageUrl.toString())
                        atvMallHistory.text = data.title
                    }
                    10 -> {
                        aivRead.setImageURI(data.imageUrl.toString())
                        atvRead.text = data.title
                    }
                    11 -> {
                        aivWatch.setImageURI(data.imageUrl.toString())
                        atvWatch.text = data.title
                    }
                }
            }
        })


        viewModel?.chatStatusLiveData?.observe(this, Observer {
            if (it?.data != null) {
                activeChatData = it.data!![0]

            } else {
                activeChatData = null
                DataManager.saveStringInPreference(PreferenceManager.CHAT_START_TIME, "")
                DataManager.saveBooleanInPreference(PreferenceManager.IS_USER_BUSY, false)
            }

            setChatWithAstrologerStatus()

        })


        viewModel?.mProfileLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.profileDataList?.let { profileData ->
                viewModel?.saveProfileDetailsInDB(profileData)
                setUserDetailsOnSideMenu()
            }

        })


        viewModel?.endChatResponse?.observe(this, Observer {
            hideProgressBar()
            logout()
        })


        viewModel?.registerFcmIdLiveData?.observe(this, Observer {
            // fcmId registered successfully
        })


    }


    private fun registerFcmId() {
        if (viewModel?.getFcmID().isNullOrBlank()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new FCM registration token and save
                val token = task.result
                DataManager.saveStringInPreference(PreferenceManager.FCM_ID, token ?: "")

                viewModel?.registerFcmId()

            })

        } else {
            viewModel?.registerFcmId()
        }
    }


    private fun setChatWithAstrologerStatus() {
        if (activeChatData != null) {
            atvLabelChat.setTextColor(
                ContextCompat.getColor(this, R.color.colorWhite)
            )
            atvLabelChatRate.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

            clChat.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorActiveGreen)


        } else {
            atvLabelChat.setTextColor(
                ContextCompat.getColor(this, R.color.colorBlack)
            )
            atvLabelChatRate.setTextColor(ContextCompat.getColor(this, R.color.colorGrayText))

            clChat.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorWhite)

        }
    }


    private fun setUpNavigationDrawer() {
        val mDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.app_name,
            R.string.app_name
        )
        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        // setting sideMenu width to match parent
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val params = rlMenu.layoutParams as DrawerLayout.LayoutParams
        params.width = displayMetrics.widthPixels
        rlMenu.layoutParams = params


        // creating side menu list
        val sideMenuList = ArrayList<SideMenuModel>()
        sideMenuList.add(
            SideMenuModel(getString(R.string.my_languages), R.drawable.ic_my_language)
        )
        sideMenuList.add(SideMenuModel(getString(R.string.rate_us), R.drawable.ic_rate_us))
        sideMenuList.add(SideMenuModel(getString(R.string.share), R.drawable.ic_share_black))

        if (isUserLoggedIn()) {
            sideMenuList.add(
                0, SideMenuModel(getString(R.string.wallet_transactions), R.drawable.ic_wallet)
            )
            sideMenuList.add(
                1, SideMenuModel(getString(R.string.order_history), R.drawable.ic_time)
            )
            sideMenuList.add(SideMenuModel(getString(R.string.logout), R.drawable.ic_logout))
        }

        // setting side menu adapter
        val sideMenuAdapter = SideMenuOptionsAdapter(sideMenuList, mClickListener = { position ->

            closeNavigationDrawer()

            when (position) {

                0 -> {
                    if (isUserLoggedIn()) {
                        // Transaction History
                        showTransactionHistory()

                    } else {
                        // My Languages
                        showMyLanguages()
                    }
                }

                1 -> {
                    if (isUserLoggedIn()) {
                        // Order History
                        showOrderHistory()

                    } else {
                        // Rate Us
                        AppUtils.openAppInGooglePlayStore(this)

                    }
                }

                2 -> {
                    if (isUserLoggedIn()) {
                        // My Languages
                        showMyLanguages()

                    } else {
                        // Share
                        shareApp()
                    }
                }

                3 -> { // Rate Us
                    AppUtils.openAppInGooglePlayStore(this)
                }

                4 -> { // Share
                    shareApp()

                }

                5 -> { // Logout
                    AppAlertDialog(this,
                        R.drawable.ic_alert,
                        getString(R.string.logout),
                        getString(R.string.sure_to_logout_from_app),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        onPositiveBtnClicked = {
                            if (activeChatData != null) {
                                viewModel?.endChat(activeChatData?.msisdn, activeChatData?.chatId)

                            } else {
                                logout()
                            }
                        }).show()
                }
            }
        })

        rvSideMenu.itemAnimator = DefaultItemAnimator()
        rvSideMenu.adapter = sideMenuAdapter


        atvAppVersion.text = getString(R.string.version)
        atvAppVersion.append("\n")
        atvAppVersion.append(BuildConfig.VERSION_NAME)

    }


    /**
     * method to set up Adapters
     */
    private fun setUpAdapters() {
        // Setting Banner Adapter
        mBannersAdapter = BannerPagerAdapter(mBannerList, clickListener = { position ->
            mBannerList[position].let {
                when (it.redirectURL) {
                    BannerRedirect.RECHARGE.value -> {
                        onWalletClicked()
                    }

                    BannerRedirect.CHAT.value -> {
                        onChatClicked()
                    }

                    BannerRedirect.TALK.value -> {
                        onCallClicked()
                    }

                    BannerRedirect.REPORT.value -> {
                        onReportClicked()
                    }

                    BannerRedirect.MALL.value -> {
                        onMallClicked()
                    }
                }
            }
        })
        vpBanners.adapter = mBannersAdapter


        // Setting SunSings Adapter
        mSunSignAdapter = SunSignListAdapter(mSunSignList, mClickListener = { position ->

            // handle item click here
            val intent = Intent(this, DailyHoroscopeActivity::class.java)
            intent.putExtra(AppConstants.KEY_POSITION, position)
            intent.putParcelableArrayListExtra(AppConstants.KEY_DATA, mSunSignList)
            startActivity(intent)
            passContentViewEvent("sun sign")

        })
        rvSunSigns.itemAnimator = DefaultItemAnimator()
        rvSunSigns.adapter = mSunSignAdapter

    }


    private fun setListeners() {
        aivMenu.setOnClickListener(this)
        rlProfile.setOnClickListener(this)
        atvNumber.setOnClickListener(this)
        aivCross.setOnClickListener(this)
        aivWallet.setOnClickListener(this)
        aivSupportChat.setOnClickListener(this)
        aivNotification.setOnClickListener(this)
        clChat.setOnClickListener(this)
        clCall.setOnClickListener(this)
        clDetailedReport.setOnClickListener(this)
        clAstroMall.setOnClickListener(this)
        rlAstroNews.setOnClickListener(this)
        llChatHistory.setOnClickListener(this)
        llCallHistory.setOnClickListener(this)
        llReportHistory.setOnClickListener(this)
        llAstroMallHistory.setOnClickListener(this)
        atvRead.setOnClickListener(this)
        atvWatch.setOnClickListener(this)
    }


    private fun openProfileScreen() {
        if (isUserLoggedIn()) {
            startActivity(Intent(this, UserProfileActivity::class.java))
            closeNavigationDrawer()
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivMenu -> {
                openNavigationDrawer()
            }

            R.id.rlProfile -> {
                openProfileScreen()
            }

            R.id.atvNumber -> {
                if (!isUserLoggedIn()) {
                    startActivity(Intent(this, LoginSignUpActivity::class.java))
                    closeNavigationDrawer()
                } else {
                    openProfileScreen()
                }
            }

            R.id.aivCross -> {
                onBackPressed()
            }

            R.id.aivWallet -> {
                onWalletClicked()
            }

            R.id.aivSupportChat -> {
                if (isUserLoggedIn()) {
                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                } else {
                    showLoginDialog()
                }
            }

            R.id.aivNotification -> {
                if (isUserLoggedIn()) {
                    val intent = Intent(this, NotificationsActivity::class.java)
                    startActivity(intent)

                } else {
                    showLoginDialog()
                }
            }

            R.id.clChat -> {
                onChatClicked()
            }

            R.id.clCall -> {
                onCallClicked()
            }

            R.id.clDetailedReport -> {
                onReportClicked()
            }

            R.id.clAstroMall -> {
                onMallClicked()
            }

            R.id.rlAstroNews -> {
                startActivity(Intent(this, AstrologyNewsActivity::class.java))
                passContentViewEvent("Astrology news ")
            }

            R.id.llChatHistory -> {
                if (isUserLoggedIn()) {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    intent.putExtra(AppConstants.KEY_TYPE, OrderHistoryType.CHAT.value)
                    startActivity(intent)
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.CHAT.value,
                        CapturedEvents.LOGIN_YES,
                        viewModel?.getPhone()!!
                    )


                } else {
                    showLoginDialog()
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.CHAT.value,
                        CapturedEvents.LOGIN_NO,
                        CapturedEvents.NO_MOB
                    )

                }
            }

            R.id.llCallHistory -> {
                if (isUserLoggedIn()) {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    intent.putExtra(AppConstants.KEY_TYPE, OrderHistoryType.CALL.value)
                    startActivity(intent)
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.CALL.value,
                        CapturedEvents.LOGIN_YES,
                        viewModel?.getPhone()!!
                    )


                } else {
                    showLoginDialog()
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.CALL.value,
                        CapturedEvents.LOGIN_NO,
                        CapturedEvents.NO_MOB
                    )

                }
            }

            R.id.llReportHistory -> {
                if (isUserLoggedIn()) {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    intent.putExtra(AppConstants.KEY_TYPE, OrderHistoryType.REPORT.value)
                    startActivity(intent)
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.REPORT.value,
                        CapturedEvents.LOGIN_YES,
                        viewModel?.getPhone()!!
                    )


                } else {
                    showLoginDialog()
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.REPORT.value,
                        CapturedEvents.LOGIN_NO,
                        CapturedEvents.NO_MOB
                    )

                }
            }

            R.id.llAstroMallHistory -> {
                if (isUserLoggedIn()) {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    intent.putExtra(AppConstants.KEY_TYPE, OrderHistoryType.MALL.value)
                    startActivity(intent)
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.MALL.value,
                        CapturedEvents.LOGIN_YES,
                        viewModel?.getPhone()!!
                    )

                } else {
                    showLoginDialog()
                    BaseApplication.instance.captureOrderHistoryEvent(
                        OrderHistoryType.MALL.value,
                        CapturedEvents.LOGIN_NO,
                        CapturedEvents.NO_MOB
                    )

                }
            }

            R.id.atvRead -> {
                startActivity(Intent(this, ReadStoryActivity::class.java))

                passContentViewEvent("read")
            }

            R.id.atvWatch -> {
                startActivity(Intent(this, WatchStoryActivity::class.java))
                passContentViewEvent("watch")
            }
        }
    }


    private fun onWalletClicked() {
        if (isUserLoggedIn()) {
            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)

        } else {
            showLoginDialog()
        }
    }


    private fun onChatClicked() {
        if (activeChatData != null) {

            val astrologerName =
                activeChatData?.firstName + " " + (activeChatData?.lastName ?: "")

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(AppConstants.KEY_CHAT_ID, activeChatData?.chatId)
            intent.putExtra(AppConstants.KEY_NUMBER, activeChatData?.msisdn)
            intent.putExtra(AppConstants.KEY_TITLE, astrologerName)
            intent.putExtra(AppConstants.KEY_IMAGE, activeChatData?.profilePicUrl)
            intent.putExtra(AppConstants.KEY_TIME, activeChatData?.chatStartTime)
            intent.putExtra(AppConstants.KEY_IS_SHOWING_HISTORY, false)
            startActivity(intent)

        } else {
            val intent = Intent(this, AstrologersListActivity::class.java)
            intent.putExtra(AppConstants.KEY_TYPE, AstrologersListType.CHAT.value)
            startActivity(intent)
        }
    }


    private fun onCallClicked() {
        val intent = Intent(this, AstrologersListActivity::class.java)
        intent.putExtra(AppConstants.KEY_TYPE, AstrologersListType.CALL.value)
        startActivity(intent)
    }


    private fun onReportClicked() {
        val intent = Intent(this, AstrologersListActivity::class.java)
        intent.putExtra(AppConstants.KEY_TYPE, AstrologersListType.REPORT.value)
        startActivity(intent)
    }


    private fun onMallClicked() {
        val intent = Intent(this, AstroMallCategoriesActivity::class.java)
        startActivity(intent)
    }


    private fun passContentViewEvent(eventTab: String) {
        if (isUserLoggedIn())
            BaseApplication.instance.captureContentViewEvent(
                eventTab,
                CapturedEvents.LOGIN_YES,
                viewModel?.getPhone()!!
            )
        else
            BaseApplication.instance.captureContentViewEvent(
                eventTab,
                CapturedEvents.LOGIN_NO,
                CapturedEvents.NO_MOB
            )
    }


    private fun showTransactionHistory() {
        val intent = Intent(this, TransactionHistoryActivity::class.java)
        startActivity(intent)
    }


    private fun showMyLanguages() {
        val intent = Intent(this, MyLanguagesActivity::class.java)
        startActivity(intent)
    }


    private fun showOrderHistory() {
        val intent = Intent(this, OrderHistoryActivity::class.java)
        intent.putExtra(AppConstants.KEY_TYPE, OrderHistoryType.ALL.value)
        startActivity(intent)
    }


    private fun shareApp() {
        AppUtils.shareApplication(
            this,
            getString(R.string.app_name),
            getString(R.string.share_app_link_message)
        )

        BaseApplication.instance.captureShareEvent(viewModel?.getPhone()!!)


    }


    private fun openNavigationDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    private fun closeNavigationDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }


    fun handleAppIntent(intentData: Intent?) {
        intentData?.let {
            if (it.hasExtra(AppConstants.KEY_TARGET_ACTION)) {
                // intent from push notification
                if (isUserLoggedIn()) {
//                    if (it.getStringExtra(AppConstants.KEY_TARGET_ACTION).equals("6", true)) {
//
//                        val actionData =
//                            it.getStringExtra(AppConstants.KEY_TARGET_ACTION_DATA) ?: ""
//
//                        if (actionData.isNotBlank() && actionData.contains("|")) {
//
//                            val actionDataArr = actionData.split("|")
//
//                            if (actionData.contains("Accepted", true)) {
//                                val newIntent = Intent(this, ChatRequestActivity::class.java)
//                                newIntent.putExtra(AppConstants.KEY_NUMBER, actionDataArr[1])
//                                newIntent.putExtra(AppConstants.KEY_NAME, actionDataArr[2])
//                                newIntent.putExtra(AppConstants.KEY_IMAGE, actionDataArr[3])
//                                startActivity(newIntent)
//                            }
//
//                        }
//                    }
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleAppIntent(intent)
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeNavigationDrawer()

        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(pushNotificationReceiver)

        super.onDestroy()
    }


}