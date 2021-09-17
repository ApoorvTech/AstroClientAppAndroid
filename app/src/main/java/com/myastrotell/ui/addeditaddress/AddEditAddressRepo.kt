package com.myastrotell.ui.addeditaddress

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.AddEditAddressRequest


class AddEditAddressRepo : BaseRepository() {

    fun getMsisdn() = DataManager.getMsisdn()


    suspend fun saveAddress(
        apiRequestCode: Int,
        request: AddEditAddressRequest,
        addEditAddressLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.saveAddress(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    addEditAddressLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun editAddress(
        apiRequestCode: Int,
        request: AddEditAddressRequest,
        addEditAddressLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.editAddress(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    addEditAddressLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }

}