package com.myastrotell.ui.orderhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.DataManager
import com.myastrotell.data.OrderHistoryType
import com.myastrotell.pojo.requests.CallRequest
import com.myastrotell.pojo.requests.ProductBillingRequest
import com.myastrotell.pojo.response.*
import kotlinx.coroutines.launch


class OrderHistoryViewModel : BaseViewModel() {

    private val mRepo = OrderHistoryRepo()

    var type = OrderHistoryType.CHAT.value


    val orderHistoryLiveData by lazy { MutableLiveData<BaseResponseModel<List<ProductBillingDetails>>>() }

    val orderDetailsLiveData by lazy { MutableLiveData<BaseResponseModel<OrderDetailResponse>>() }

    val astrologersStatusListLiveDate by lazy { MutableLiveData<BaseResponseModel<List<AstrologersStatusResponse>>>() }

    val busyAstrologersListLiveData by lazy { MutableLiveData<BaseResponseModel<List<String>>>() }

    val reportStatusLiveData by lazy { MutableLiveData<BaseResponseModel<List<OrderDetailResponse>>>() }

    val astrologerStatusLiveData by lazy { MutableLiveData<BaseResponseModel<AstrologerStatusByMsisdnResponse>>() }

    val callRequestLiveData by lazy { MutableLiveData<BaseResponseModel<CallRequestResponse>>() }



    fun getOrderHistory(type: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val request = ProductBillingRequest(searchKey = type)

                mRepo.getOrderHistory(
                    ApiRequestCodes.PRODUCT_BILLING_DETAILS,
                    request,
                    orderHistoryLiveData,
                    errorLiveData
                )

            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun getOrderDetails(orderId: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                mRepo.getOrderDetails(
                    ApiRequestCodes.ORDER_DETAILS,
                    orderId,
                    orderDetailsLiveData,
                    errorLiveData
                )

            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * getting list of astrologers with status of availability
     */
    fun getAllAstrologersStatus() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getAllAstrologersStatus(
                    ApiRequestCodes.GET_BUSY_PANDITS,
                    astrologersStatusListLiveDate,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * getting list of astrologers who are busy
     */
    fun getBusyAstrologersList() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getBusyAstrologersList(
                    ApiRequestCodes.GET_BUSY_PANDITS,
                    busyAstrologersListLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * getting status of astrologer by msisdn
     */
    fun getAstrologerStatusByNumber(astrologerMsisdn: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getAstrologerStatusByNumber(
                    ApiRequestCodes.GET_ASTROLOGER_BUSY_STATUS,
                    astrologerMsisdn,
                    astrologerStatusLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * getting status of report for user
     */
    fun getReportStatusForUser() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getReportStatusForUser(
                    ApiRequestCodes.GET_REPORT_STATUS,
                    reportStatusLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }



    /**
     * method to init call request
     */
    fun initCallRequest(astrologerId: String, price: String, balance: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                val callRequest = CallRequest(
                    astrologerId,
                    price,
                    balance,
                    DataManager.getMsisdn()
                )

                mRepo.callRequest(
                    ApiRequestCodes.CALL_REQUEST,
                    callRequest,
                    callRequestLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


}