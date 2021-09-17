package com.myastrotell.ui.astromall

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.BaseApplication
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.pojo.response.RedemptionSearchResponse
import com.myastrotell.pojo.response.SearchByCategoryResponse
import com.trackier.sdk.TrackierEvent
import com.trackier.sdk.TrackierSDK
import kotlinx.coroutines.launch

class AstroMallViewModel : BaseViewModel() {

    private val mRepo = AstroMallRepo()

    val mallCategoryLiveData by lazy { MutableLiveData<BaseResponseModel<SearchByCategoryResponse>>() }

    val mallItemsLiveData by lazy { MutableLiveData<BaseResponseModel<List<RedemptionSearchResponse>>>() }

    val cartItemsLiveData by lazy { MutableLiveData<List<CartEntity>>() }


    val cartItemCount by lazy { ObservableInt(0) }

    val isAddedToCart by lazy { ObservableBoolean(false) }


    fun getMallCategories() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getMallCategories(
                    ApiRequestCodes.SEARCHBYCATEGGORY,
                    mallCategoryLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }

    fun getMallItems(redemptionCategory: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getMallItems(
                    ApiRequestCodes.REDEMPTION_SEARCH,
                    redemptionCategory,
                    mallItemsLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun getCartItems() {
        viewModelScope.launch {
            cartItemsLiveData.value = mRepo.getCartItems()
        }
    }


    fun addItemToCart(
        item: RedemptionSearchResponse,
        quantity: Int
    ) {
        viewModelScope.launch {

            val cartEntity = CartEntity()
            cartEntity.productId = item.goodsId
            cartEntity.productName = item.goodsName
            cartEntity.productDescription = item.goodsDescription
            cartEntity.productThumb = item.goodsImage
            cartEntity.productPrice = item.goodsPrice.toString()
            cartEntity.quantity = quantity
            cartEntity.consultantName = ""
            cartEntity.category = item.goodsCategory

            mRepo.addItemToCart(cartEntity)

            setCartItemCount()
            BaseApplication.instance.captureAddToCartEvent(item)



        }
    }




    fun setCartItemCount() {
        viewModelScope.launch {
            cartItemCount.set(mRepo.getCartItemCount())
        }
    }


    fun isAddedToCart(productId: String) {
        viewModelScope.launch {
            isAddedToCart.set(mRepo.isAddedToCart(productId))
        }
    }

}