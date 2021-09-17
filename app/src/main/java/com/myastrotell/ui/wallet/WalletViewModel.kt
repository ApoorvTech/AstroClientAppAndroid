package com.myastrotell.ui.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.AuthenticateVoucherResponse
import com.myastrotell.pojo.response.OfferListResponse
import com.myastrotell.pojo.response.WalletBalanceResponse
import kotlinx.coroutines.launch


class WalletViewModel : BaseViewModel() {

    private val mRepo = WalletRepo()

    val offerListLiveData by lazy { MutableLiveData<BaseResponseModel<List<OfferListResponse>>>() }
    val walletBalanceLiveData by lazy { MutableLiveData<BaseResponseModel<WalletBalanceResponse>>() }
    val authenticateVoucherLiveData by lazy { MutableLiveData<BaseResponseModel<AuthenticateVoucherResponse>>() }

    val paymentSummaryLiveData by lazy { MutableLiveData<BaseResponseModel<OfferListResponse>>() }


    fun getOfferList() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getOffersList(
                    ApiRequestCodes.OFFER_LIST,
                    offerListLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun getWalletBalance(showProgress: Boolean) {
        if (navigator?.isNetworkAvailable() == true) {

            if (showProgress)
                navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getWalletBalance(ApiRequestCodes.WALLET_BALANCE, walletBalanceLiveData)
            }

        }
    }


    fun authenticateVoucher(voucherCode: String, productName: String, amount: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.authenticateVoucher(
                    ApiRequestCodes.AUTHENTICATE_VOUCHER,
                    voucherCode, productName, amount,
                    authenticateVoucherLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun getPaymentSummary(amount: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getPaymentSummary(
                    ApiRequestCodes.AUTHENTICATE_VOUCHER,
                    amount,
                    paymentSummaryLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


}