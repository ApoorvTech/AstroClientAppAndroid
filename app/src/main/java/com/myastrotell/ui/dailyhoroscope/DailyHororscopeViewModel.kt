package com.myastrotell.ui.dailyhoroscope

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.HororsopeResponse
import kotlinx.coroutines.launch


class DailyHororscopeViewModel : BaseViewModel() {
    private val mRepo = DailyHororscopeRepo()

    val horoscopeLiveData by lazy { MutableLiveData<BaseResponseModel<HororsopeResponse>>() }


    fun getHoroscope(sunSign: String?) {
        if (navigator?.isNetworkAvailable() == true){
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getHoroscope(ApiRequestCodes.HOROSCOPE, horoscopeLiveData, errorLiveData, sunSign)
            }

        }else{
            navigator?.showNoNetworkError()
        }
    }

}