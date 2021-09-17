package com.myastrotell.ui.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.VoucherCategory
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.pojo.requests.buymallproduct.BuyProductRequest
import com.myastrotell.pojo.requests.buymallproduct.ProductInfo
import com.myastrotell.pojo.requests.buymallproduct.ShippingAddress
import com.myastrotell.pojo.requests.buymallproduct.ValidateMallVoucherRequest
import com.myastrotell.pojo.response.AddressListResponse
import com.myastrotell.pojo.response.ValidateMallVoucherResponse
import kotlinx.coroutines.launch


class CartViewModel : BaseViewModel() {
    private val mRepo = CartRepo()

    val cartItemsLiveData by lazy { MutableLiveData<List<CartEntity>>() }

    val addressListLiveData by lazy { MutableLiveData<BaseResponseModel<List<AddressListResponse>>>() }

    val deleteAddressLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }
    val buyProductLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }
    val buyProductStatusLiveData by lazy { MutableLiveData<Boolean>() }

    val authenticateVoucherLiveData by lazy { MutableLiveData<BaseResponseModel<ValidateMallVoucherResponse>>() }


    fun getMsisdn() = mRepo.getMsisdn()


    fun getCartItems() {
        viewModelScope.launch {
            cartItemsLiveData.value = mRepo.getCartItems()
        }
    }

    fun getAddressList() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getAddressList(
                    ApiRequestCodes.GET_ADDRESS_LIST,
                    addressListLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun deleteAddress(addressId: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.deleteAddress(
                    ApiRequestCodes.DELETE_ADDRESS,
                    addressId,
                    deleteAddressLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            mRepo.updateQuantity(productId, quantity)
        }
    }

    fun updateConsultantName(productId: String, consultantName: String) {
        viewModelScope.launch {
            mRepo.updateConsultantName(productId, consultantName)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            mRepo.emptyCart()
        }
    }

    fun removeItemFromCart(productId: String) {
        viewModelScope.launch {
            mRepo.removeItemFromCart(productId)
        }
    }


    fun authenticateVoucher(
        mCartItemsList: ArrayList<CartEntity>,
        voucherCode: String,
        amount: String
    ) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val productList = ArrayList<ProductInfo>()

                mCartItemsList.forEach { data ->
                    val requestData = ProductInfo(
                        redeemCategory = data.category ?: "",
                        redeemPoint = data.total.toString(),
                        redeemMode = data.productName ?: "",
                        redeemValue = data.productPrice ?: "",
                        redeemUnit = data.quantity.toString(),
                        redeemId = data.productId ?: ""
                    )
                    productList.add(requestData)
                }

                val request = ValidateMallVoucherRequest(
                    products = productList,
                    productName = VoucherCategory.ASTRO_MALL.value,
                    promocode = voucherCode,
                    totalRedeemPoints = amount
                )

                mRepo.authenticateMallVoucher(
                    ApiRequestCodes.AUTHENTICATE_VOUCHER,
                    request,
                    authenticateVoucherLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun buyProduct(
        cartItemsList: ArrayList<CartEntity>,
        address: AddressListResponse,
        total: Double,
        voucherTransactionId: String?
    ) {

        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val list = ArrayList<ProductInfo>()

                cartItemsList.forEach { data ->

                    val requestData = ProductInfo(
                        redeemCategory = data.category ?: "",
                        redeemPoint = data.total.toString(),
                        redeemMode = data.productName ?: "",
                        redeemValue = data.productPrice ?: "",
                        redeemUnit = data.quantity.toString(),
                        redeemId = data.productId ?: ""
                    )

                    list.add(requestData)
                }

                val shippingAddress = ShippingAddress(
                    deliveryName = address.userName ?: "",
                    deliveryMobileNo = address.mobile ?: "",
                    houseNo = address.houseNo ?: "",
                    addressLine1 = address.addressLine1 ?: "",
                    addressLine2 = address.addressLine2 ?: "",
                    pinCode = address.pinCode ?: "",
                    city = address.city ?: "",
                    state = address.state ?: "",
                    country = address.country ?: "India"
                )

                val request = BuyProductRequest(
                    products = list,
                    redemptionAddress = "",
                    address = shippingAddress,
                    totalRedeemPoints = total,
                    paytmNumber = "0",
                    isQuantityCheck = "0",
                    isSelfPaytm = "0",
                    voucher_transaction_id = voucherTransactionId
                )

                mRepo.buyProduct(
                    request,
                    ApiRequestCodes.PRODUCT_BUY,
                    buyProductLiveData,
                    errorLiveData,
                    buyProductStatusLiveData
                )

            }

        } else {
            navigator?.showNoNetworkError()

        }
    }

}