package com.myastrotell.ui.astrologynews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.pojo.response.SearchByCategoryResponse
import kotlinx.coroutines.launch

class AstrologyNewsViewModel : BaseViewModel() {

    private val mRepo = AstrologyNewsRepo()

    val newsLiveData by lazy { MutableLiveData<BaseResponseModel<SearchByCategoryResponse>>() }


    fun getNews() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getReadStory(ApiRequestCodes.ASTRO_NEWS, newsLiveData, errorLiveData)
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }
}