package com.myastrotell.ui.cart

import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.DeleteAddressRequest
import com.myastrotell.pojo.requests.GetAddressRequest
import com.myastrotell.pojo.requests.buymallproduct.BuyProductRequest
import com.myastrotell.pojo.requests.buymallproduct.ValidateMallVoucherRequest
import com.myastrotell.pojo.response.AddressListResponse
import com.myastrotell.pojo.response.ValidateMallVoucherResponse


class CartRepo : BaseRepository() {

    fun getMsisdn() = DataManager.getMsisdn()


    suspend fun getCartItems(): List<CartEntity> {
        return DataManager.getCartData()
    }

    suspend fun updateQuantity(productId: String, quantity: Int) {
        return DataManager.updateQuantity(productId, quantity)
    }

    suspend fun updateConsultantName(productId: String, consultantName: String) {
        return DataManager.updateConsultantName(productId, consultantName)
    }

    suspend fun emptyCart() {
        DataManager.clearCart()
    }


    suspend fun removeItemFromCart(productId: String) {
        DataManager.removeItemFromCart(productId)
        DataManager.getCartItemsCount()
    }



    suspend fun authenticateMallVoucher(
        apiStatusCode: Int,
        request: ValidateMallVoucherRequest,
        authenticateVoucherLiveData: MutableLiveData<BaseResponseModel<ValidateMallVoucherResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiStatusCode) {
            DataManager.authenticateMallVoucher(request)
        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    val response = it.response as? BaseResponseModel<ValidateMallVoucherResponse>
                    response?.data?.promoCode = request.promocode

                    authenticateVoucherLiveData.value = response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun buyProduct(
        request: BuyProductRequest, apiRequestCode: Int,
        addressListLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>,
        statusLiveData: MutableLiveData<Boolean>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.buyProduct(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {

                    val balance =
                        DataManager.getStringFromPreference(PreferenceManager.WALLET_BALANCE)

                    if (balance.isNotBlank()) {
                        val updatedBalance = balance.toDouble() - request.totalRedeemPoints

                        // updating wallet balance
                        DataManager.saveStringInPreference(
                            PreferenceManager.WALLET_BALANCE, updatedBalance.toString()
                        )
                    }

                    addressListLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    statusLiveData.value=false
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun getAddressList(
        apiRequestCode: Int,
        addressListLiveData: MutableLiveData<BaseResponseModel<List<AddressListResponse>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = GetAddressRequest(DataManager.getMsisdn())

        safeApiCall(apiRequestCode) {

            DataManager.getAddressList(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    addressListLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


    suspend fun deleteAddress(
        apiRequestCode: Int,
        addressId: String,
        addressListLiveData: MutableLiveData<BaseResponseModel<Any>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val request = DeleteAddressRequest(addressId)

        safeApiCall(apiRequestCode) {

            DataManager.deleteAddress(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    addressListLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }
    }


}