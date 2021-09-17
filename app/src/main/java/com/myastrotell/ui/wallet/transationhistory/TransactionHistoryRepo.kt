package com.myastrotell.ui.wallet.transationhistory

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.WalletPassbookRequest
import com.myastrotell.pojo.response.WalletTransactionResponse


class TransactionHistoryRepo : BaseRepository(){

   suspend fun getTransactionHistory(
       apiRequestCode: Int,
        transactionHistoryLiveData: MutableLiveData<BaseResponseModel<List<WalletTransactionResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

       val request = WalletPassbookRequest()

        safeApiCall(apiRequestCode) {

            DataManager.getWalletTransactions(request)

        }.let {
            when (it){
                is ResultWrapper.Success -> {
                    transactionHistoryLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }
}