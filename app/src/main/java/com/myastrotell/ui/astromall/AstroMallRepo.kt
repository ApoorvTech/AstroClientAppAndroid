package com.myastrotell.ui.astromall

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.Category
import com.myastrotell.data.DataManager
import com.myastrotell.data.RedemptionType
import com.myastrotell.data.SubCategory
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.RedemptionSearchRequest
import com.myastrotell.pojo.requests.SearchByCategoryRequest
import com.myastrotell.pojo.response.RedemptionSearchResponse
import com.myastrotell.pojo.response.SearchByCategoryResponse

class AstroMallRepo : BaseRepository() {

    suspend fun getMallCategories(
        apiRequestCode: Int,
        mallCategoryLiveData: MutableLiveData<BaseResponseModel<SearchByCategoryResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = SearchByCategoryRequest(
            Category.MALL.value,
            SubCategory.CAT_NAMES.value
        )

        safeApiCall(apiRequestCode) {

            DataManager.getSearchByCategory(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    mallCategoryLiveData.value = it.response
                }
                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }

    suspend fun getMallItems(
        apiRequestCode: Int,
        redeemCategory: String,
        storyLiveData: MutableLiveData<BaseResponseModel<List<RedemptionSearchResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = RedemptionSearchRequest(
            redeemCategory,
            RedemptionType.WHITE.value
        )

        safeApiCall(apiRequestCode) {

            DataManager.getRedemptionSearchItems(request)

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

    suspend fun getCartItems(): List<CartEntity> {
        return DataManager.getCartData()
    }

    suspend fun addItemToCart(cartEntity: CartEntity) {
        DataManager.addItemInCart(cartEntity)
    }

    suspend fun isAddedToCart(productId: String): Boolean {
        return DataManager.getCartItemByProductId(productId) != null
    }

    suspend fun getCartItemCount(): Int {
        return DataManager.getCartItemsCount()
    }

}