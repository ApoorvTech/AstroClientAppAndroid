package com.myastrotell.ui.orderhistory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.OrderHistoryListAdapter
import com.myastrotell.base.BaseFragment
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.*
import com.myastrotell.databinding.FragmentOrderHistoryBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.interfaces.OrderHistoryItemClickListener
import com.myastrotell.pojo.response.ProductBillingDetails
import com.myastrotell.ui.chat.ChatActivity
import com.myastrotell.ui.detailform.DetailsFormActivity
import com.myastrotell.ui.mallorderdetails.MallOrderDetailsActivity
import com.myastrotell.ui.reportdetails.ReportDetailActivity
import com.myastrotell.ui.wallet.WalletActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.invisible
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.fragment_order_history.*
import kotlinx.android.synthetic.main.layout_no_data_found.*


class OrderHistoryFragment : BaseFragment<FragmentOrderHistoryBinding, OrderHistoryViewModel>() {

    private lateinit var mOrdersAdapter: OrderHistoryListAdapter
    private lateinit var mOrdersList: ArrayList<ProductBillingDetails>

    private var selectedPosition: Int = -1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

        setupAdapter()

        rvOrderHistory.invisible()

        viewModel?.getOrderHistory(viewModel?.type!!)

    }


    override fun getLayoutId() = R.layout.fragment_order_history

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<OrderHistoryViewModel>()

    override fun initVariables() {
        mOrdersList = ArrayList()

    }


    private fun getData() {
        viewModel?.type = arguments?.getString(AppConstants.KEY_TYPE) ?: OrderHistoryType.CHAT.value

    }


    override fun setObservers() {
        viewModel?.orderHistoryLiveData?.observe(viewLifecycleOwner, Observer {
            hideProgressBar()

            mOrdersList.clear()

            it.data?.let { list ->
                mOrdersList.addAll(list)
                mOrdersAdapter.notifyDataSetChanged()
            }

            if (mOrdersList.isEmpty()) {
                atvNoDataPlaceholder.visible()

            } else {
                atvNoDataPlaceholder.gone()
                when (viewModel?.type) {
                    OrderHistoryType.CHAT.value, OrderHistoryType.CALL.value, OrderHistoryType.REPORT.value -> {
                        viewModel?.getAllAstrologersStatus()
                        viewModel?.getBusyAstrologersList()
                    }

                    else -> {
                        rvOrderHistory.visible()
                    }
                }
            }
        })


        viewModel?.astrologersStatusListLiveDate?.observe(viewLifecycleOwner, Observer {
            hideProgressBar()

            it.data?.forEach { model ->
                for (item in mOrdersList) {
                    if (item.redeemMode.equals(model.productId, true)
                        && viewModel?.type.equals(model.productType, true)
                    ) {
                        item.isOnline = true
                    }
                }
            }
            mOrdersAdapter.notifyDataSetChanged()
            rvOrderHistory.visible()
        })


        viewModel?.busyAstrologersListLiveData?.observe(viewLifecycleOwner, Observer {
            hideProgressBar()

            it.data?.forEach { msisdn ->
                for (item in mOrdersList) {
                    if (item.redeemMode.equals(msisdn, true)) {
                        item.isBusy = true
                    }
                }
            }
            mOrdersAdapter.notifyDataSetChanged()
            rvOrderHistory.visible()
        })


        viewModel?.astrologerStatusLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.busy?.let { isBusy ->
                if (isBusy) {
                    AppAlertDialog(
                        requireContext(),
                        R.drawable.ic_alert,
                        getString(R.string.alert),
                        getString(R.string.astrologer_is_busy),
                        getString(R.string.ok),
                        "",
                        onPositiveBtnClicked = {
                            mOrdersList[selectedPosition].isBusy = true
                            mOrdersAdapter.notifyItemChanged(selectedPosition)
                        }
                    ).show()

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


        viewModel?.reportStatusLiveData?.observe(viewLifecycleOwner, Observer {
            hideProgressBar()

            if (it?.data.isNullOrEmpty()) {
                openIntakeFormScreen()

            } else {
                AppAlertDialog(
                    requireContext(),
                    R.drawable.ic_alert,
                    getString(R.string.alert),
                    getString(R.string.you_have_request_in_queue_please_try_later),
                    getString(R.string.ok),
                    "",
                    onPositiveBtnClicked = {}
                ).show()
            }

        })


        viewModel?.callRequestLiveData?.observe(this, Observer {
            hideProgressBar()

            AppAlertDialog(
                requireContext(),
                R.drawable.ic_checked,
                getString(R.string.success),
                getString(R.string.your_call_request_has_been_submitted),
                getString(R.string.ok),
                null,
                onPositiveBtnClicked = {

                }
            ).show()

        })

    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        if (responseModel?.code == ApiStatusCodes.NO_DATA_FOUND) {

            when (responseModel.apiRequestCode) {

                ApiRequestCodes.PRODUCT_BILLING_DETAILS, ApiRequestCodes.ALL_ASTROLOGERS_STATUS, ApiRequestCodes.GET_BUSY_PANDITS -> {
                    if (mOrdersList.isEmpty()) {
                        atvNoDataPlaceholder.visible()

                    } else {
                        atvNoDataPlaceholder.gone()
                        rvOrderHistory.visible()
                    }
                }

                ApiRequestCodes.GET_REPORT_STATUS -> {
                    if (responseModel.code == ApiStatusCodes.NO_DATA_FOUND) {
                        openIntakeFormScreen()
                    }
                }

                else -> {
                    super.handleApiErrorResponse(responseModel)
                }
            }

        } else {
            super.handleApiErrorResponse(responseModel)
        }
    }


    private fun setupAdapter() {
        mOrdersAdapter =
            OrderHistoryListAdapter(
                mOrdersList,
                viewModel?.type!!,
                object : OrderHistoryItemClickListener {

                    override fun onActionClicked(position: Int) {
                        selectedPosition = position
                        val data = mOrdersList[position]

                        when (viewModel?.type) {
                            AstrologersListType.CHAT.value -> {

                                val balance: Double = getWalletBalance().toDouble()
                                val requiredBalance: Double = (data.redeemValue ?: 0.00) * 5
                                if (balance < requiredBalance) {
                                    showInsufficientBalanceAlert(
                                        String.format(
                                            getString(R.string.minimumBalanceForChat),
                                            requiredBalance,
                                            data.productName
                                        )
                                    )
                                    return
                                }

                                viewModel?.getAstrologerStatusByNumber(data.redeemMode.toString())

                            }

                            AstrologersListType.CALL.value -> {

                                val balance: Double = getWalletBalance().toDouble()
                                val requiredBalance: Double = (data.redeemValue ?: 0.00) * 5
                                if (balance < requiredBalance) {
                                    showInsufficientBalanceAlert(
                                        String.format(
                                            getString(R.string.minimumBalanceForCall),
                                            requiredBalance,
                                            data.productName
                                        )
                                    )
                                    return
                                }

                                viewModel?.getAstrologerStatusByNumber(data.redeemMode.toString())

                            }

                            AstrologersListType.REPORT.value -> {

                                val balance: Double = getWalletBalance().toDouble()
                                val requiredBalance: Double = (data.redeemValue ?: 0.00)
                                if (balance < requiredBalance) {
                                    showInsufficientBalanceAlert(
                                        String.format(
                                            getString(R.string.minimumBalanceToProceed),
                                            requiredBalance
                                        )
                                    )
                                    return
                                }

                                viewModel?.getReportStatusForUser()

                            }
                        }
                    }

                    override fun onItemClicked(position: Int) {
                        mOrdersList[position].let { data ->

                            when (viewModel?.type) {

                                OrderHistoryType.CHAT.value -> {
                                    val intent =
                                        Intent(requireContext(), ChatActivity::class.java)
                                    intent.putExtra(AppConstants.KEY_ID, data.id)
                                    intent.putExtra(AppConstants.KEY_CHAT_ID, "")
                                    intent.putExtra(AppConstants.KEY_NUMBER, data.redeemMode)
                                    intent.putExtra(AppConstants.KEY_TITLE, data.productName)
                                    intent.putExtra(AppConstants.KEY_IMAGE, data.imageURL)
                                    intent.putExtra(AppConstants.KEY_DATE, data.redeemDate)
                                    intent.putExtra(AppConstants.KEY_IS_SHOWING_HISTORY, true)
                                    startActivity(intent)
                                }

                                OrderHistoryType.CALL.value -> {
//                                    val intent =
//                                        Intent(requireContext(), CallReviewActivity::class.java)
//                                    intent.putExtra(AppConstants.KEY_DATA, data)
//                                    startActivity(intent)
                                }

                                OrderHistoryType.REPORT.value -> {
                                    val intent =
                                        Intent(requireContext(), ReportDetailActivity::class.java)
                                    intent.putExtra(AppConstants.KEY_DATA, data)
                                    startActivity(intent)
                                }

                                OrderHistoryType.MALL.value -> {
                                    val intent = Intent(
                                        requireContext(), MallOrderDetailsActivity::class.java
                                    )
                                    intent.putExtra(AppConstants.KEY_DATA, data)
                                    startActivity(intent)

                                }
                            }

                        }
                    }

                })

        rvOrderHistory.itemAnimator = DefaultItemAnimator()
        rvOrderHistory.adapter = mOrdersAdapter

    }


    /**
     * method to open Intake Form Screen
     */
    private fun openIntakeFormScreen() {
        if (isFragmentAdded()) {
            mOrdersList[selectedPosition].let { data ->
                val intent = Intent(requireContext(), DetailsFormActivity::class.java)
                intent.putExtra(AppConstants.KEY_TYPE, viewModel?.type)
                intent.putExtra(AppConstants.KEY_ID, data.redeemMode)
                intent.putExtra(AppConstants.KEY_TITLE, data.productName)
                intent.putExtra(AppConstants.KEY_IMAGE, data.imageURL)
                intent.putExtra(AppConstants.KEY_PRICE, data.redeemValue)
                intent.putExtra(AppConstants.KEY_LANGUAGES, "English")
                startActivityForResult(intent, AppConstants.DETAIL_FORM_REQUEST_CODE)
            }
        }
    }


    /**
     * method to initiate call request
     */
    private fun initCallRequest() {
        if (isFragmentAdded()) {
            mOrdersList[selectedPosition].let { data ->
                viewModel?.initCallRequest(
                    data.redeemMode.toString(),
                    data.redeemValue.toString(),
                    getWalletBalance()
                )
            }
        }
    }


    private fun showInsufficientBalanceAlert(message: String) {
        AppAlertDialog(
            requireContext(),
            R.drawable.ic_alert,
            getString(R.string.alert),
            message,
            getString(R.string.recharge),
            getString(R.string.cancel), onPositiveBtnClicked = {

                val intent = Intent(requireContext(), WalletActivity::class.java)
                startActivity(intent)

            }
        ).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DETAIL_FORM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel?.getOrderHistory(viewModel?.type!!)
                }
            }
        }
    }


}