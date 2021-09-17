package com.myastrotell.ui.wallet.transationhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.WalletTransactionResponse
import kotlinx.coroutines.launch


class TransactionHistoryViewModel : BaseViewModel() {
    private val mRepo = TransactionHistoryRepo()

    val transactionHistoryLiveData by lazy { MutableLiveData<BaseResponseModel<List<WalletTransactionResponse>>>() }


    fun getTransactionHistory(){
        if (navigator?.isNetworkAvailable() == true){
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getTransactionHistory(ApiRequestCodes.PASSBOOK, transactionHistoryLiveData, errorLiveData)
            }

        } else{
            navigator?.showNoNetworkError()
        }
    }

}