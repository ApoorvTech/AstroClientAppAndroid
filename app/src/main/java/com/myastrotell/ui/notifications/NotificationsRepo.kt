package com.myastrotell.ui.notifications

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.InAppNotificationReq
import com.myastrotell.pojo.response.NotificationData

class NotificationsRepo : BaseRepository() {


    suspend fun getNotifications(
        apiRequestCode: Int,
        notificationResponse: MutableLiveData<BaseResponseModel<NotificationData>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = InAppNotificationReq()

        safeApiCall(apiRequestCode) {

            DataManager.getNotificationsList(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    notificationResponse.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


}