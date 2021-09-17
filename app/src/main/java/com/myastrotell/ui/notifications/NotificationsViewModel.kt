package com.myastrotell.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.NotificationData
import kotlinx.coroutines.launch

class NotificationsViewModel: BaseViewModel() {

    private val mRepo = NotificationsRepo()

    val notificationsData by lazy { MutableLiveData<BaseResponseModel<NotificationData>>() }


    fun getNotifications() {
        if (navigator?.isNetworkAvailable() == true){
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getNotifications(ApiRequestCodes.NOTIFICATIONS, notificationsData, errorLiveData)
            }

        }else{
            navigator?.showNoNetworkError()
        }
    }
}