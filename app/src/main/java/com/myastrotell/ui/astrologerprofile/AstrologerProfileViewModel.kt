package com.myastrotell.ui.astrologerprofile

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.DataManager
import com.myastrotell.pojo.requests.CallRequest
import com.myastrotell.pojo.response.AstrologerStatusByMsisdnResponse
import com.myastrotell.pojo.response.AstrologersStatusResponse
import com.myastrotell.pojo.response.CallRequestResponse
import com.myastrotell.pojo.response.astrlogerprofile.AstrologerProfileWithRateResponse
import com.myastrotell.pojo.response.astrlogerprofile.ItemInfo
import com.myastrotell.pojo.response.astrlogerprofile.ReviewModel
import kotlinx.coroutines.launch

class AstrologerProfileViewModel : BaseViewModel() {

    private val mRepo = AstrologerProfileRepo()

    var isAvailableForCall: Boolean = false
    var isAvailableForChat: Boolean = false

    lateinit var astrologerId: String
    lateinit var astrologerName: String
    lateinit var astrologerImage: String

    var chatRate: Double = 0.0
    var callRate: Double = 0.0
    var reportRate: Double = 0.0


    val astrologerProfileWithRateLiveDate by lazy { MutableLiveData<BaseResponseModel<List<AstrologerProfileWithRateResponse>>>() }
    val astrologersReviewListLiveDate by lazy { MutableLiveData<BaseResponseModel<List<ReviewModel>>>() }
    val astrologersProductInfoListLiveDate by lazy { MutableLiveData<BaseResponseModel<List<ItemInfo>>>() }
    val astrologersStatusListLiveDate by lazy { MutableLiveData<BaseResponseModel<List<AstrologersStatusResponse>>>() }

    val astrologerStatusLiveData by lazy { MutableLiveData<BaseResponseModel<AstrologerStatusByMsisdnResponse>>() }

    val callRequestLiveData by lazy { MutableLiveData<BaseResponseModel<CallRequestResponse>>() }


    fun setIntent(intent: Intent?) {
        astrologerId = intent?.getStringExtra(AppConstants.KEY_ID) ?: ""
        astrologerName = intent?.getStringExtra(AppConstants.KEY_TITLE) ?: ""
        astrologerImage = intent?.getStringExtra(AppConstants.KEY_IMAGE) ?: ""
    }


    fun isUserBusy(): Boolean {
        return mRepo.isUserBusy()
    }


    fun getAstrologersDetails() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                /**
                 * getting basic details for astrologers with rate
                 */
                mRepo.getAstrologersDetailsWithRate(
                    ApiRequestCodes.ASTROLOGERS_LIST,
                    astrologerProfileWithRateLiveDate,
                    errorLiveData,
                    astrologerId
                )

                /**
                 * getting data for total time for chat, call and total reports till now
                 */
                mRepo.getProductTimeDetails(
                    ApiRequestCodes.ASTROLOGER_TIME,
                    astrologersProductInfoListLiveDate,
                    errorLiveData,
                    astrologerId
                )

                /**
                 * getting list of reviews
                 */
                mRepo.getAstroReview(
                    ApiRequestCodes.ASTROLOGER_REVIEW,
                    astrologersReviewListLiveDate,
                    errorLiveData,
                    astrologerId
                )

                /**
                 * getting list of astrologers with status of availability for Call/Chat/Report
                 */
                mRepo.getAllAstrologersStatus(
                    ApiRequestCodes.ALL_ASTROLOGERS_STATUS,
                    astrologersStatusListLiveDate,
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