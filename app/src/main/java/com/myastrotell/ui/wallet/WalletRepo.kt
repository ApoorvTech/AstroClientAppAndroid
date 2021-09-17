package com.myastrotell.ui.wallet

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.AuthenticateVoucherRequest
import com.myastrotell.pojo.response.AuthenticateVoucherResponse
import com.myastrotell.pojo.response.OfferListResponse
import com.myastrotell.pojo.response.WalletBalanceResponse


class WalletRepo : BaseRepository() {


    suspend fun getOffersList(
        apiRequestCode: Int,
        walletBalanceLiveData: MutableLiveData<BaseResponseModel<List<OfferListResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
        safeApiCall(apiRequestCode) {

            DataManager.getOfferList()

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    walletBalanceLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun getWalletBalance(
        apiRequestCode: Int,
        walletBalanceLiveData: MutableLiveData<BaseResponseModel<WalletBalanceResponse>>
    ) {
        safeApiCall(apiRequestCode) {

            DataManager.getWalletBalance()

        }.let {
            if (it is ResultWrapper.Success) {
                it.response?.data?.myPoint?.point?.let { points ->
                    DataManager.saveStringInPreference(PreferenceManager.WALLET_BALANCE, points)
                }
                walletBalanceLiveData.value = it.response
            }
        }
    }


    suspend fun authenticateVoucher(
        apiStatusCode: Int,
        voucherCode: String,
        productName: String,
        amount: String,
        authenticateVoucherLiveData: MutableLiveData<BaseResponseModel<AuthenticateVoucherResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = AuthenticateVoucherRequest(
            voucherCode,
            productName,
            amount
        )

        safeApiCall(apiStatusCode) {
            DataManager.authenticateVoucher(request)
        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    authenticateVoucherLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getPaymentSummary(
        apiStatusCode: Int,
        amount: String,
        authenticateVoucherLiveData: MutableLiveData<BaseResponseModel<OfferListResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {
        safeApiCall(apiStatusCode) {

            DataManager.getPaymentSummary(amount)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    authenticateVoucherLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


}