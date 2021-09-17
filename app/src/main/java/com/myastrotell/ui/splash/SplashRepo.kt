package com.myastrotell.ui.splash

import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.response.AppUpdateResponse


class SplashRepo : BaseRepository() {

    suspend fun appUpdated(): ResultWrapper<BaseResponseModel<AppUpdateResponse>?> {
        return safeApiCall(0) { DataManager.appUpdate() }
    }

}