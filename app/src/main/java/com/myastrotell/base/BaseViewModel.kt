package com.myastrotell.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.myastrotell.data.DataManager


open class BaseViewModel : ViewModel() {

    val errorLiveData = MutableLiveData<BaseResponseModel<*>>()

    var navigator: BaseNavigator? = null

    /**
     * [errorObserver] must be attached with [errorLiveData]
     */
    fun setErrorObserver(owner: LifecycleOwner, errorObserver: Observer<BaseResponseModel<*>>) {
        errorLiveData.observe(owner, errorObserver)
    }

    fun getPhone():String{
       return DataManager.getMsisdn()
    }


    override fun onCleared() {
        navigator = null
        super.onCleared()
    }

}