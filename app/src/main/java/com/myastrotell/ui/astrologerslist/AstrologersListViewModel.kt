package com.myastrotell.ui.astrologerslist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AstrologersListType
import com.myastrotell.data.DataManager
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.pojo.requests.CallRequest
import com.myastrotell.pojo.response.AstrologerListResponse
import com.myastrotell.pojo.response.AstrologerStatusByMsisdnResponse
import com.myastrotell.pojo.response.CallRequestResponse
import com.myastrotell.pojo.response.OrderDetailResponse
import kotlinx.coroutines.launch


class AstrologersListViewModel : BaseViewModel() {

    private val mRepo = AstrologersListRepo()

    var type: String = AstrologersListType.CHAT.value

    val selectedLanguageList by lazy { MutableLiveData<List<SelectedLanguagesEntity>>() }

    val astrologersListLiveDate by lazy { MutableLiveData<BaseResponseModel<ArrayList<AstrologerListResponse>>>() }

    val busyAstrologersListLiveData by lazy { MutableLiveData<BaseResponseModel<List<String>>>() }

    val astrologerStatusLiveData by lazy { MutableLiveData<BaseResponseModel<AstrologerStatusByMsisdnResponse>>() }

    val reportStatusLiveData by lazy { MutableLiveData<BaseResponseModel<List<OrderDetailResponse>>>() }

    val subscribeForOnlineStatusLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }

    val callRequestLiveData by lazy { MutableLiveData<BaseResponseModel<CallRequestResponse>>() }


    fun isUserBusy(): Boolean {
        return mRepo.isUserBusy()
    }


    /**
     * getting list of selected languages
     */
    fun getSelectedLanguages() {
        viewModelScope.launch {
            selectedLanguageList.value = mRepo.getSelectedLanguages()
        }
    }


    /**
     * getting list of astrologers
     */
    fun getAstrologersList(type: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getAstrologersList(
                    ApiRequestCodes.ASTROLOGERS_LIST,
                    astrologersListLiveDate,
                    errorLiveData,
                    type
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
     * api to subscribe For Online Status
     */
    fun subscribeForOnlineStatus(productId: String?) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.subscribeForOnlineStatus(
                    ApiRequestCodes.SUBSCRIBE_FOR_ONLINE_STATUS,
                    productId,
                    type,
                    subscribeForOnlineStatusLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * api to subscribe For Availability Status
     */
    fun subscribeForAvailableStatus(productId: String?) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.subscribeForAvailableStatus(
                    ApiRequestCodes.SUBSCRIBE_FOR_AVAILABLE_STATUS,
                    productId,
                    type,
                    subscribeForOnlineStatusLiveData,
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