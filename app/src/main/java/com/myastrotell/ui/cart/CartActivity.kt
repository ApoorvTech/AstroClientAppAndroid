package com.myastrotell.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.CartItemsAdapter
import com.myastrotell.adapters.SavedAddressListAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.CapturedEvents
import com.myastrotell.data.CustomEvents
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.databinding.ActivityCartBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.dialogs.ApplyVoucherCodeDialog
import com.myastrotell.interfaces.SavedAddressItemClickListener
import com.myastrotell.pojo.response.AddressListResponse
import com.myastrotell.pojo.response.ValidateMallVoucherResponse
import com.myastrotell.ui.addeditaddress.AddEditAddressActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import com.trackier.sdk.TrackierEvent
import com.trackier.sdk.TrackierSDK
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*
import java.lang.StringBuilder


class CartActivity : BaseActivity<ActivityCartBinding, CartViewModel>(), View.OnClickListener {

    private var deleteAddressPosition = -1

    private lateinit var mCartItemsAdapter: CartItemsAdapter
    private lateinit var mCartItemsList: ArrayList<CartEntity>

    private lateinit var mAddressAdapter: SavedAddressListAdapter
    private lateinit var mAddressList: ArrayList<AddressListResponse>

    private var voucherDialog: ApplyVoucherCodeDialog? = null
    private var voucherData: ValidateMallVoucherResponse? = null

    private var total: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.your_cart)
        aetNumber?.setText(viewModel?.getMsisdn())

        atvNoDataPlaceholder.text = getString(R.string.your_cart_is_empty)

        setupAdapters()

        viewModel?.getCartItems()
        viewModel?.getAddressList()

    }


    override fun getLayoutId() = R.layout.activity_cart

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<CartViewModel>()

    override fun initVariables() {
        mCartItemsList = ArrayList()
        mAddressList = ArrayList()

    }

    override fun setObservers() {

        viewModel?.buyProductStatusLiveData?.observe(this, Observer {

            captureItemPurchaseEvent(false)
        })

        viewModel?.buyProductLiveData?.observe(this, Observer {

            hideProgressBar()

            captureItemPurchaseEvent(true)

            viewModel?.clearCart()

            AppAlertDialog(
                this,
                R.drawable.ic_checked,
                getString(R.string.success),
                getString(R.string.your_order_has_been_placed),
                getString(R.string.ok),
                null,
                onPositiveBtnClicked = {
                    finish()
                }
            ).show()

        })


        viewModel?.authenticateVoucherLiveData?.observe(this, Observer {
            hideProgressBar()

            voucherDialog?.dismiss()

            it?.data?.let { data ->
                voucherData = data
                val discountStr = ("₹${String.format("%.2f", data.totalDiscount ?: 0.0)}")

                atvLabelDiscount.visible()
                atvDiscountAmount.visible()

                atvDiscountAmount.text = ("- $discountStr")

                atvCouponTitle.text = data.promoCode
                atvCouponMessage.text = ("$discountStr promocode applied for this transaction.")

                atvApplyVoucher.gone()
                rlAppliedCoupon.visible()

                updateTotal()

            }

        })


        viewModel?.cartItemsLiveData?.observe(this, Observer {

            mCartItemsList.clear()

            it?.let { list ->
                mCartItemsList.addAll(list)
                mCartItemsAdapter.notifyDataSetChanged()
            }

            updateTotal()

        })


        viewModel?.addressListLiveData?.observe(this, Observer {
            hideProgressBar()

            mAddressList.clear()

            it?.data?.let { list ->
                mAddressList.addAll(list)
                mAddressAdapter.notifyDataSetChanged()
            }

            if (mAddressList.isEmpty()) {
                atvLabelSelectAddress.gone()

            } else {
                atvLabelSelectAddress.visible()
            }

            if (mAddressList.size > 4) {
                atvAddNewAddress.gone()

            } else {
                atvAddNewAddress.visible()
            }

            updateView()

        })


        viewModel?.deleteAddressLiveData?.observe(this, Observer {
            hideProgressBar()

            showShortToast(it?.msg)

            if (deleteAddressPosition >= 0 && deleteAddressPosition < mAddressList.size) {

                mAddressList.removeAt(deleteAddressPosition)
                mAddressAdapter.selectedPosition = 0
                mAddressAdapter.notifyDataSetChanged()

            }

            if (mAddressList.isEmpty()) {
                atvLabelSelectAddress.gone()

            } else {
                atvLabelSelectAddress.visible()
            }

            if (mAddressList.size > 4) {
                atvAddNewAddress.gone()

            } else {
                atvAddNewAddress.visible()
            }


        })


        aivBack.setOnClickListener(this)
        atvApplyVoucher.setOnClickListener(this)
        aivRemoveCoupon.setOnClickListener(this)
        atvAddNewAddress.setOnClickListener(this)
        atvBuyNow.setOnClickListener(this)

    }






    private fun setupAdapters() {
        // setting cart items adapter
        mCartItemsAdapter = CartItemsAdapter(mCartItemsList,

            onQuantityUpdated = { position ->
                viewModel?.updateQuantity(
                    mCartItemsList[position].productId ?: "",
                    mCartItemsList[position].quantity
                )

                updateTotal()

            },

            onDeleteClicked = { position ->
                AppAlertDialog(this,
                    R.drawable.ic_alert,
                    getString(R.string.delete_item),
                    getString(R.string.sure_to_delete_item_from_cart),
                    getString(R.string.confirm),
                    getString(R.string.cancel),
                    onPositiveBtnClicked = {
                        viewModel?.removeItemFromCart(mCartItemsList[position].productId ?: "")

                        mCartItemsList.removeAt(position)
                        mCartItemsAdapter.notifyItemRemoved(position)

                        updateTotal()
                        updateView()

                    }
                ).show()
            },

            onConsultantUpdated = { position, consultantName ->
                viewModel?.updateConsultantName(
                    mCartItemsList[position].productId ?: "",
                    consultantName
                )
            }

        )
        rvCartItems.itemAnimator = DefaultItemAnimator()
        rvCartItems.adapter = mCartItemsAdapter


        // setting saved address adapter
        mAddressAdapter =
            SavedAddressListAdapter(mAddressList, object : SavedAddressItemClickListener {

                override fun onEditClicked(position: Int) {
                    val intent = Intent(this@CartActivity, AddEditAddressActivity::class.java)
                    intent.putExtra(AppConstants.KEY_DATA, mAddressList[position])
                    startActivityForResult(intent, AppConstants.ADD_EDIT_ADDRESS_REQUEST_CODE)
                }

                override fun onDeleteClicked(position: Int) {
                    AppAlertDialog(this@CartActivity,
                        R.drawable.ic_alert,
                        getString(R.string.delete_address),
                        getString(R.string.sure_to_delete_address),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        onPositiveBtnClicked = {
                            deleteAddressPosition = position
                            viewModel?.deleteAddress(mAddressList[position].addressId ?: "")

                        }
                    ).show()
                }

                override fun onItemSelected(position: Int) {

                }

            })
        rvAddresses.itemAnimator = DefaultItemAnimator()
        rvAddresses.adapter = mAddressAdapter

    }


    private fun updateTotal() {
        total = 0.0

        mCartItemsList.forEach {
            total += (it.productPrice?.toDouble() ?: 0.0).times(it.quantity)
        }

        atvSubTotalAmount.text = String.format(getString(R.string.money_format, total))

        if (voucherData != null) {
            if (total < voucherData?.min_amount ?: 0.0) {
                removeVoucher()
            } else {
                total -= voucherData?.totalDiscount ?: 0.0
            }
        }

        atvTotalAmount.text = String.format(getString(R.string.money_format, total))

    }


    private fun updateView() {
        if (mCartItemsList.isEmpty()) {
            atvNoDataPlaceholder.visible()
            llMain.gone()
            atvBuyNow.gone()

        } else {
            atvNoDataPlaceholder.gone()
            llMain.visible()
            atvBuyNow.visible()
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvApplyVoucher -> {
                if (mCartItemsList.isNotEmpty()) {
                    if (voucherDialog == null) {
                        voucherDialog = ApplyVoucherCodeDialog(this,
                            applyButtonClick = { voucherCode ->
                                voucherDialog?.hideMessage()
                                if (voucherCode.isBlank()) {
                                    voucherDialog?.showMessage(getString(R.string.enter_voucher_code))

                                } else {
                                    viewModel?.authenticateVoucher(
                                        mCartItemsList,
                                        voucherCode,
                                        total.toString()
                                    )
                                }
                            }
                        )
                    }
                    voucherDialog?.show()
                    voucherDialog?.reset()
                }
            }


            R.id.aivRemoveCoupon -> {
                removeVoucher()
                updateTotal()
            }

            R.id.atvAddNewAddress -> {
                val intent = Intent(this, AddEditAddressActivity::class.java)
                startActivityForResult(intent, AppConstants.ADD_EDIT_ADDRESS_REQUEST_CODE)
            }

            R.id.atvBuyNow -> {
                if (checkValidation()) {
                    viewModel?.buyProduct(
                        mCartItemsList,
                        mAddressList[mAddressAdapter.selectedPosition],
                        total,
                        voucherData?.voucher_transaction_id ?: ""
                    )
                }

            }
        }
    }


    private fun removeVoucher() {
        voucherData = null
        rlAppliedCoupon.gone()
        atvApplyVoucher.visible()

        atvLabelDiscount.gone()
        atvDiscountAmount.gone()

    }


    private fun checkValidation(): Boolean {
        if (mCartItemsList.size == 0) {
            showShortToast(getString(R.string.add_items_in_cart_to_proceed))
            return false

        } else if (mAddressList.size == 0) {
            showShortToast(getString(R.string.please_add_address_to_proceed))
            return false

        } else if (getWalletBalance().toDouble() < total) {
            AppAlertDialog(
                this,
                R.drawable.ic_alert,
                getString(R.string.alert),
                getString(R.string.minimumBalanceToProceed, total),
                getString(R.string.ok),
                "",
                null
            ).show()
            return false
        }

        return true
    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        when (responseModel?.apiRequestCode) {
            ApiRequestCodes.AUTHENTICATE_VOUCHER -> {
                voucherDialog?.showMessage(responseModel.msg)
            }

            else -> {
                super.handleApiErrorResponse(responseModel)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.ADD_EDIT_ADDRESS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    // refresh address list here
                    viewModel?.getAddressList()

                }
            }
        }
    }



    private fun captureItemPurchaseEvent(status:Boolean)
    {
        val ids=StringBuilder("")
        val names=StringBuilder("")
        val quantities=StringBuilder("")
        val price=StringBuilder("")



        mCartItemsList.forEachIndexed{index,element->

            if(!(index==0 || index==mCartItemsList.size)){
                ids.append(",")
                names.append(",")
                quantities.append(",")
                price.append(",")
            }
            ids.append(element.productId)
            names.append(element.productName)
            quantities.append(element.quantity.toString())
            price.append(element.productPrice)


        }
        val event = TrackierEvent(CustomEvents.MallPurchase)

        val customEventMap=HashMap<String,Any>()
        customEventMap["Product ID"] = ids.toString()
        customEventMap["Product Price"] = price.toString()
        customEventMap["Total"] = total
        customEventMap["Product Name"] = names.toString()
        customEventMap["Product Quantity"] = quantities.toString()
        if(status)
        customEventMap[CapturedEvents.STATUS] = CapturedEvents.SUCCES
        else
        customEventMap[CapturedEvents.STATUS] = CapturedEvents.FAIL

        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)
    }


}