package com.myastrotell.ui.home.watchstory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.SearchByCategoryResponse
import kotlinx.coroutines.launch

class WatchStoryViewModel: BaseViewModel() {
    private val mRepo = WatchStoryRepo()

    val watchStoryLiveData by lazy { MutableLiveData<BaseResponseModel<SearchByCategoryResponse>>() }


    fun getWatchStory() {
        if (navigator?.isNetworkAvailable() == true){
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getWatchStory(ApiRequestCodes.OUR_STORY, watchStoryLiveData, errorLiveData)
            }
        }else{
            navigator?.showNoNetworkError()
        }
    }
}