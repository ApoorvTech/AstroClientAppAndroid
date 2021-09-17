package com.myastrotell.ui.home.readstory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.SearchByCategoryResponse
import kotlinx.coroutines.launch

class ReadStoryViewModel: BaseViewModel() {

    private val mRepo = ReadStoryRepo()

    val readStoryLiveData by lazy { MutableLiveData<BaseResponseModel<SearchByCategoryResponse>>() }


    fun getReadStory() {
        if (navigator?.isNetworkAvailable() == true){
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getReadStory(ApiRequestCodes.OUR_STORY, readStoryLiveData, errorLiveData)
            }
        }else{
            navigator?.showNoNetworkError()
        }
    }

}