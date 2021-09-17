package com.myastrotell.ui.addeditaddress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.requests.AddEditAddressRequest
import kotlinx.coroutines.launch


class AddEditAddressViewModel : BaseViewModel() {
    private val mRepo = AddEditAddressRepo()

    val addEditAddressLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }


    fun getMsisdn() = mRepo.getMsisdn()


    fun saveAddress(request: AddEditAddressRequest) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.saveAddress(
                    ApiRequestCodes.SAVE_ADDRESS,
                    request,
                    addEditAddressLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun editAddress(request: AddEditAddressRequest) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.editAddress(
                    ApiRequestCodes.EDIT_ADDRESS,
                    request,
                    addEditAddressLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


}