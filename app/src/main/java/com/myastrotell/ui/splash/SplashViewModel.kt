package com.myastrotell.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.response.AppUpdateResponse
import kotlinx.coroutines.launch


class SplashViewModel : BaseViewModel() {
    private val mRepo = SplashRepo()

    val appUpdateLiveData by lazy { MutableLiveData<BaseResponseModel<AppUpdateResponse>>() }


    fun getAppUpdate(){
        if (navigator?.isNetworkAvailable() == true){
            viewModelScope.launch {
             mRepo.appUpdated().let{
                 when(it){
                     is ResultWrapper.Success ->{
                         appUpdateLiveData.value = it.response
                     }

                     is ResultWrapper.GenericError -> {
                         errorLiveData.value = it.response
                     }
                 }
             }
            }
        } else{
            navigator?.showNoNetworkError()
        }
    }

}