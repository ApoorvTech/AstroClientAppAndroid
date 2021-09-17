package com.myastrotell.ui.astrologynews

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.Category
import com.myastrotell.data.DataManager
import com.myastrotell.data.SubCategory
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.SearchByCategoryRequest
import com.myastrotell.pojo.response.SearchByCategoryResponse

class AstrologyNewsRepo: BaseRepository() {

    suspend fun getReadStory(
        apiRequestCode: Int,
        storyLiveData: MutableLiveData<BaseResponseModel<SearchByCategoryResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = SearchByCategoryRequest(
            Category.NEWS.value,
            SubCategory.NEWS_SUBCATEGORY.value
        )

        safeApiCall(apiRequestCode) {

            DataManager.getSearchByCategory(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    storyLiveData.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }
}