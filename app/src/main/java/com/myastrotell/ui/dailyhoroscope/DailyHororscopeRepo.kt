package com.myastrotell.ui.dailyhoroscope

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.Category
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.HoroscopeRequest
import com.myastrotell.pojo.response.HororsopeResponse


class DailyHororscopeRepo : BaseRepository() {


    suspend fun getHoroscope(
        apiRequestCode: Int,
        horoscopeLiveData: MutableLiveData<BaseResponseModel<HororsopeResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        sunSign: String?
    ) {

        val request = HoroscopeRequest(
            Category.HOROSCOPE.value,
            sunSign
        )

        safeApiCall(apiRequestCode) {

            DataManager.getHoroscope(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    horoscopeLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


}