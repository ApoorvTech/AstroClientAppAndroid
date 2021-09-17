package com.myastrotell.ui.astrologerslist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.BaseApplication
import com.myastrotell.R
import com.myastrotell.adapters.AstrologersListAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.*
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.databinding.ActivityAstrologersListBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.interfaces.AstrologerItemClickListener
import com.myastrotell.pojo.response.AstrologerListResponse
import com.myastrotell.pojo.response.MyComparator
import com.myastrotell.ui.astrologerprofile.AstrologerProfileActivity
import com.myastrotell.ui.detailform.DetailsFormActivity
import com.myastrotell.ui.wallet.WalletActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.invisible
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_astrologers_list.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*
import kotlinx.android.synthetic.main.layout_wallet_recharge_header.*
import java.util.*
import kotlin.collections.ArrayList


class AstrologersListActivity :
    BaseActivity<ActivityAstrologersListBinding, AstrologersListViewModel>(),
    View.OnClickListener {

    private lateinit var mAstrologersAdapter: AstrologersListAdapter
    private lateinit var mAstrologersList: ArrayList<AstrologerListResponse>

    private lateinit var mSelectedLanguageList: ArrayList<SelectedLanguagesEntity>

    private var selectedSortId: Int = 0
    private var selectedPosition: Int = -1
    private var amount: Double = 0.0
    private var astroName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        setUpAdapter()

        if (!isUserLoggedIn()) {
            rlWalletRecharge.gone()
        }

        rvAstrologers.invisible()

        viewModel?.getSelectedLanguages()

        getAstrologersList()

    }


    override fun onResume() {
        super.onResume()
        atvBalance.text = getString(R.string.currency)
        atvBalance.append(getWalletBalance())
    }


    override fun getLayoutId() = R.layout.activity_astrologers_list


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<AstrologersListViewModel>()


    override fun initVariables() {
        mAstrologersList = ArrayList()
        mSelectedLanguageList = ArrayList()

    }


    override fun setObservers() {
        viewModel?.selectedLanguageList?.observe(this, Observer {
            mSelectedLanguageList.clear()
            it?.let { list ->
                mSelectedLanguageList.addAll(list)
            }

        })


        viewModel?.astrologersListLiveDate?.observe(this, Observer {
            hideProgressBar()

            mAstrologersList.clear()
            mAstrologersAdapter.updateList(it.data)

            if (mAstrologersList.size > 0) {
                atvNoDataPlaceholder.gone()

                // getting list of busy astrologers to update busy status
                viewModel?.getBusyAstrologersList()

            } else {
                atvNoDataPlaceholder.visible()
            }

        })


        viewModel?.busyAstrologersListLiveData?.observe(this, Observer {
            hideProgressBar()

            it.data?.forEach { msisdn ->
                for (item in mAstrologersList) {
                    if (item.goodsId.equals(msisdn, true)) {
                        item.isBusy = true
                        break
                    }
                }
            }

            // sorting list by language preference
            sortAstrologersBySelectedLanguages()


//            // sorting online pandits by "sequence"
//
//            var onlinePandits=ArrayList<AstrologerListResponse>()
//            for (i in 0 until mAstrologersList.size) {
//                if (mAstrologersList[i].goodsSale == 1)
//                    onlinePandits.add(mAstrologersList[i])
//            }
//            onlinePandits.sortBy {
//                it.sequence?.toInt()
//            }


            // sorting list by online status
            var index = 0
            for (i in 0 until mAstrologersList.size) {
                if (mAstrologersList[i].goodsSale == 1) {
                    mAstrologersList.add(index, mAstrologersList.removeAt(i))
                    index++
                }
            }


            //mAstrologersList.sortWith(compareBy<AstrologerListResponse> {it.goodsSale}.thenBy { it.sequence?.toInt() })

           // mAstrologersList.reverse()
            if(index>1)
             mAstrologersList.sortWith(MyComparator())

            mAstrologersAdapter.notifyDataSetChanged()
            rvAstrologers.visible()

        })


        viewModel?.astrologerStatusLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.busy?.let { isBusy ->
                if (isBusy) {
                    AppAlertDialog(
                        this,
                        R.drawable.ic_alert,
                        getString(R.string.alert),
                        getString(R.string.astrologer_is_busy),
                        getString(R.string.ok),
                        "",
                        onPositiveBtnClicked = {
                            getAstrologersList()
                        }
                    ).show()


                    //logging event
                    when (viewModel?.type) {
                        AstrologersListType.CHAT.value -> {
                            BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateChatEvent,AstrologersListType.CHAT.value,viewModel?.getPhone()!!, amount,CapturedEvents.BUSY,astroName)

                        }

                        AstrologersListType.CALL.value -> {
                            BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateCallEvent,AstrologersListType.CALL.value,viewModel?.getPhone()!!, amount,CapturedEvents.BUSY,astroName)

                        }
                    }


                } else {
                    when (viewModel?.type) {
                        AstrologersListType.CHAT.value -> {
                            openIntakeFormScreen()
                        }

                        AstrologersListType.CALL.value -> {
                            initCallRequest()
                        }
                    }
                }
            }

        })


        viewModel?.reportStatusLiveData?.observe(this, Observer {
            hideProgressBar()

            if (it?.data.isNullOrEmpty()) {
                openIntakeFormScreen()

            } else {
                BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateReportEvent,AstrologersListType.REPORT.value,viewModel?.getPhone()!!, amount,CapturedEvents.QUEUE,astroName)

                AppAlertDialog(
                    this,
                    R.drawable.ic_alert,
                    getString(R.string.alert),
                    getString(R.string.you_have_request_in_queue_please_try_later),
                    getString(R.string.ok),
                    "",
                    onPositiveBtnClicked = {}
                ).show()
            }

        })


        viewModel?.subscribeForOnlineStatusLiveData?.observe(this, Observer {
            hideProgressBar()

            AppAlertDialog(
                this,
                R.drawable.ic_alert,
                getString(R.string.waitlist_joined),
                it?.msg,
                getString(R.string.ok),
                "",
                onPositiveBtnClicked = {}
            ).show()

        })


        viewModel?.callRequestLiveData?.observe(this, Observer {
            hideProgressBar()


            BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateCallEvent,AstrologersListType.CALL.value,viewModel?.getPhone()!!, amount,CapturedEvents.SUBMITED,astroName)

            AppAlertDialog(
                this,
                R.drawable.ic_checked,
                getString(R.string.success),
                getString(R.string.your_call_request_has_been_submitted),
                getString(R.string.ok),
                null,
                onPositiveBtnClicked = {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            ).show()

        })


        aivBack.setOnClickListener(this)
        aivSearch.setOnClickListener(this)
        aivEnd.setOnClickListener(this)
        atvRecharge.setOnClickListener(this)
        atvFilter.setOnClickListener(this)
        atvExperienceHTL.setOnClickListener(this)
        atvExperienceLTH.setOnClickListener(this)
        atvTotalOrdersHTL.setOnClickListener(this)
        atvTotalOrdersLTH.setOnClickListener(this)
        atvPriceHTL.setOnClickListener(this)
        atvPriceLTH.setOnClickListener(this)
        flFilterOptions.setOnClickListener(this)


        aetSearch.doAfterTextChanged {
            mAstrologersAdapter.filter.filter(it.toString())
        }

    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        if (responseModel?.code == ApiStatusCodes.NO_DATA_FOUND) {
            when (responseModel.apiRequestCode) {

                ApiRequestCodes.ASTROLOGERS_LIST, ApiRequestCodes.GET_BUSY_PANDITS -> {
                    if (mAstrologersList.isEmpty()) {
                        atvNoDataPlaceholder.visible()
                        rvAstrologers.invisible()

                    } else {
                        atvNoDataPlaceholder.gone()
                        rvAstrologers.visible()
                    }
                }

                ApiRequestCodes.GET_REPORT_STATUS -> {
                    openIntakeFormScreen()
                }

                else -> {
                    super.handleApiErrorResponse(responseModel)
                }
            }

        } else {
            super.handleApiErrorResponse(responseModel)
        }
    }


    private fun getAstrologersList() {
        if (!isFinishing && !isDestroyed) {
            viewModel?.getAstrologersList(viewModel?.type!!)
        }
    }


    // sorting Astrologers list by selected language
    private fun sortAstrologersBySelectedLanguages() {
        if (!isFinishing && !isDestroyed) {
            if (mSelectedLanguageList.isNotEmpty() && mAstrologersList.isNotEmpty()) {
                var index: Int
                for (i in 0 until mSelectedLanguageList.size) {
                    index = 0
                    for (j in 0 until mAstrologersList.size) {
                        if (mAstrologersList[j].goodsLanguage.toString().toLowerCase()
                                .contains(
                                    mSelectedLanguageList[i].value.toString().toLowerCase(),
                                    true
                                )
                        ) {
                            mAstrologersList.add(index, mAstrologersList.removeAt(j))
                            index++
                        }
                    }
                }
            }
        }
    }


    private fun getData() {
        viewModel?.type =
            intent?.getStringExtra(AppConstants.KEY_TYPE) ?: AstrologersListType.CHAT.value

        when (viewModel?.type) {
            AstrologersListType.CHAT.value -> {
                atvTitle.text = getString(R.string.chat_with_astrologer)
            }

            AstrologersListType.CALL.value -> {
                atvTitle.text = getString(R.string.talk_to_astrologer)
            }

            AstrologersListType.REPORT.value -> {
                atvTitle.text = getString(R.string.select_astrologer)
            }
        }

        aivSearch.visibility = View.VISIBLE
        llOptions.visibility = View.VISIBLE

    }


    /**
     * Setting Astrologers Adapter
     */
    private fun setUpAdapter() {
        mAstrologersAdapter =
            AstrologersListAdapter(
                mAstrologersList,
                viewModel?.type!!,
                object : AstrologerItemClickListener {

                    override fun onItemClicked(position: Int) {
                        mAstrologersList[position].let {
                            val intent = Intent(
                                this@AstrologersListActivity, AstrologerProfileActivity::class.java
                            )
                            intent.putExtra(AppConstants.KEY_ID, it.goodsId)
                            intent.putExtra(AppConstants.KEY_TITLE, it.goodsName)
                            intent.putExtra(AppConstants.KEY_IMAGE, it.goodsImage)
                            intent.putExtra(AppConstants.KEY_PRICE, it.goodsPrice)
                            intent.putExtra(AppConstants.KEY_TIME, it.goodsAttribute.toString())
                            intent.putExtra(AppConstants.KEY_IS_BUSY, it.isBusy)
                            intent.putExtra(AppConstants.KEY_AVG_RATING, it.goodsAvgRating)
                            intent.putExtra(AppConstants.KEY_TOTAL_RATING, it.goodsTotalRating)
                            startActivityForResult(
                                intent,
                                AppConstants.ASTROLOGER_PROFILE_REQUEST_CODE
                            )

                        }

                    }

                    override fun onBellIconClicked(position: Int) {
                        if (isUserLoggedIn()) {
                            mAstrologersList[position].let { data ->
                                if (data.goodsSale == 0) {
                                    // astrologer is offline,
                                    // subscribe for push when he comes online
                                    viewModel?.subscribeForOnlineStatus(data.goodsId)

                                } else {
                                    // astrologer is online, but busy,
                                    // subscribe for push when he becomes available again
                                    viewModel?.subscribeForAvailableStatus(data.goodsId)
                                }
                            }
                        } else {
                            showLoginDialog()
                        }
                    }

                    override fun onActionClicked(position: Int) {
                        if (isUserLoggedIn()) {

                            selectedPosition = position
                            val data = mAstrologersList[position]

                            when (viewModel?.type) {
                                AstrologersListType.CHAT.value -> {

                                    if (viewModel?.isUserBusy() == true) {
                                        showUserBusyAlert()
                                        return
                                    }

                                    val balance: Double = getWalletBalance().toDouble()
                                    val requiredBalance: Double = (data.goodsPrice ?: 0.00) * 5
                                    amount= data.goodsPrice ?: 0.0
                                    astroName= data.goodsName ?: ""

                                    if (balance < requiredBalance) {
                                        showInsufficientBalanceAlert(
                                            String.format(
                                                getString(R.string.minimumBalanceForChat),
                                                requiredBalance,
                                                data.goodsName
                                            )
                                        )
                                        return
                                    }

                                    viewModel?.getAstrologerStatusByNumber(data.goodsId.toString())

                                }

                                AstrologersListType.CALL.value -> {

                                    if (viewModel?.isUserBusy() == true) {
                                        showUserBusyAlert()
                                        return
                                    }

                                    val balance: Double = getWalletBalance().toDouble()
                                    val requiredBalance: Double = (data.goodsPrice ?: 0.00) * 5

                                    if (balance < requiredBalance) {
                                        showInsufficientBalanceAlert(
                                            String.format(
                                                getString(R.string.minimumBalanceForCall),
                                                requiredBalance,
                                                data.goodsName
                                            )
                                        )
                                        return
                                    }

                                    amount= data.goodsPrice ?: 0.0
                                    astroName= data.goodsName ?: ""

                                    viewModel?.getAstrologerStatusByNumber(data.goodsId.toString())




                                }

                                AstrologersListType.REPORT.value -> {

                                    val balance: Double = getWalletBalance().toDouble()
                                    val requiredBalance: Double = (data.goodsPrice ?: 0.00)
                                    amount= (data.goodsPrice ?: 0.00)
                                    astroName= data.goodsName ?: ""



                                    if (balance < requiredBalance) {
                                        showInsufficientBalanceAlert(
                                            String.format(
                                                getString(R.string.minimumBalanceToProceed),
                                                requiredBalance
                                            )
                                        )
                                        return
                                    }

                                    // checking if user has any report request in queue or not
                                    viewModel?.getReportStatusForUser()

                                }
                            }

                        } else {
                            showLoginDialog()
                        }
                    }

                },
                onSearchFinished = { size ->
                    if (size > 0)
                        atvNoDataPlaceholder.gone()
                    else
                        atvNoDataPlaceholder.visible()
                })

        rvAstrologers.itemAnimator = DefaultItemAnimator()
        rvAstrologers.adapter = mAstrologersAdapter

    }


    /**
     * method to open Intake Form Screen
     */
    private fun openIntakeFormScreen() {
        if (!isFinishing && !isDestroyed) {
            mAstrologersList[selectedPosition].let { data ->
                val intent = Intent(
                    this@AstrologersListActivity, DetailsFormActivity::class.java
                )
                intent.putExtra(AppConstants.KEY_TYPE, viewModel?.type)
                intent.putExtra(AppConstants.KEY_ID, data.goodsId)
                intent.putExtra(AppConstants.KEY_TITLE, data.goodsName)
                intent.putExtra(AppConstants.KEY_IMAGE, data.goodsImage)
                intent.putExtra(AppConstants.KEY_PRICE, data.goodsPrice)
                intent.putExtra(AppConstants.KEY_LANGUAGES, data.goodsLanguage)
                startActivityForResult(intent, AppConstants.DETAIL_FORM_REQUEST_CODE)
            }
        }
    }


    /**
     * method to initialize call request
     */
    private fun initCallRequest() {
        if (!isFinishing && !isDestroyed) {
            mAstrologersList[selectedPosition].let { data ->
                amount= data.goodsPrice?:0.0
                astroName= data.goodsName?:""

                viewModel?.initCallRequest(
                    data.goodsId.toString(),
                    data.goodsPrice.toString(),
                    getWalletBalance()


                )

            }
        }
    }


    @SuppressLint("ResourceType")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.aivSearch -> {
                rlSearchSort.visible()
                if (flFilterOptions.isVisible) {
                    flFilterOptions.gone()

                } else {
                    aetSearch.visible()
                    atvFilter.gone()
                }
            }

            R.id.aivEnd -> { // Filter Icon
                rlSearchSort.visible()
                if (flFilterOptions.isVisible) {
                    flFilterOptions.gone()

                } else {
                    if (!aetSearch.text?.trim().toString().isBlank())
                        aetSearch.setText("")

                    aetSearch.gone()

                    atvFilter.visible()
                }
            }

            R.id.atvRecharge -> {
                val intent = Intent(this, WalletActivity::class.java)
                startActivity(intent)
            }

            R.id.atvFilter -> { // Show Filters
                flFilterOptions.visible()
            }

            R.id.atvExperienceHTL -> {
                flFilterOptions.gone()
                atvFilter.text = getString(R.string.experience_high_to_low)
                mAstrologersAdapter.sortList(v.id, selectedSortId)
                selectedSortId = v.id
            }

            R.id.atvExperienceLTH -> {
                flFilterOptions.gone()
                atvFilter.text = getString(R.string.experience_low_to_high)
                mAstrologersAdapter.sortList(v.id, selectedSortId)
                selectedSortId = v.id
            }

            R.id.atvTotalOrdersHTL -> {
                flFilterOptions.gone()
                atvFilter.text = getString(R.string.total_orders_high_to_low)
                mAstrologersAdapter.sortList(v.id, selectedSortId)
                selectedSortId = v.id
            }

            R.id.atvTotalOrdersLTH -> {
                flFilterOptions.gone()
                atvFilter.text = getString(R.string.total_orders_low_to_high)
                mAstrologersAdapter.sortList(v.id, selectedSortId)
                selectedSortId = v.id
            }

            R.id.atvPriceHTL -> {
                flFilterOptions.gone()
                atvFilter.text = getString(R.string.price_high_to_low)
                mAstrologersAdapter.sortList(v.id, selectedSortId)
                selectedSortId = v.id
            }

            R.id.atvPriceLTH -> {
                flFilterOptions.gone()
                atvFilter.text = getString(R.string.price_low_to_high)
                mAstrologersAdapter.sortList(v.id, selectedSortId)
                selectedSortId = v.id
            }

            R.id.flFilterOptions -> {
                flFilterOptions.gone()
            }

        }
    }


    private fun showUserBusyAlert() {
        AppAlertDialog(
            this@AstrologersListActivity,
            R.drawable.ic_alert,
            getString(R.string.alert),
            getString(R.string.you_cant_proceed_as_you_are_busy),
            getString(R.string.ok),
            null,
            onPositiveBtnClicked = {}
        ).show()
    }


    private fun showInsufficientBalanceAlert(message: String) {
        AppAlertDialog(
            this,
            R.drawable.ic_alert,
            getString(R.string.alert),
            message,
            getString(R.string.recharge),
            getString(R.string.cancel), onPositiveBtnClicked = {
                val intent = Intent(this, WalletActivity::class.java)
                startActivity(intent)
            }
        ).show()
    }


    override fun onBackPressed() {
        if (flFilterOptions.isVisible) {
            flFilterOptions.gone()
        } else {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DETAIL_FORM_REQUEST_CODE, AppConstants.ASTROLOGER_PROFILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    getAstrologersList()
                }
            }
        }
    }


}