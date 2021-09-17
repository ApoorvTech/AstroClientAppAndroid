package com.myastrotell.ui.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.WalletOfferAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.VoucherCategory
import com.myastrotell.databinding.ActivityWalletBinding
import com.myastrotell.dialogs.ApplyVoucherCodeDialog
import com.myastrotell.pojo.response.AuthenticateVoucherResponse
import com.myastrotell.pojo.response.OfferListResponse
import com.myastrotell.ui.wallet.paymentinformation.PaymentInformationActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class WalletActivity : BaseActivity<ActivityWalletBinding, WalletViewModel>(),
    View.OnClickListener {

    private var voucherDialog: ApplyVoucherCodeDialog? = null
    private var voucherData: AuthenticateVoucherResponse? = null

    private lateinit var mOfferAdapter: WalletOfferAdapter
    private lateinit var mOfferAmountList: ArrayList<OfferListResponse>

    private var isAmountSelectedFromCards = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.add_money_to_wallet)

        setUpAdapter()

        getUpdatedWalletBalance()
        viewModel?.getOfferList()

    }


    override fun onResume() {
        super.onResume()

        setWalletBalance()

    }


    override fun getLayoutId() = R.layout.activity_wallet

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<WalletViewModel>()

    override fun initVariables() {
        mOfferAmountList = ArrayList()
    }

    override fun setObservers() {

        viewModel?.offerListLiveData?.observe(this, Observer {
            hideProgressBar()

            mOfferAmountList.clear()

            it?.data?.let { offerList ->
                mOfferAmountList.addAll(offerList)
            }

            mOfferAdapter.notifyDataSetChanged()

        })


        viewModel?.walletBalanceLiveData?.observe(this, Observer {
            hideProgressBar()
            setWalletBalance()
        })


        viewModel?.authenticateVoucherLiveData?.observe(this, Observer {
            hideProgressBar()

            voucherDialog?.dismiss()

            it?.data?.let { data ->
                voucherData = data
                atvApplyVoucher.gone()

                atvCouponTitle.text = data.promocode
                atvCouponMessage.text = ("₹${
                    String.format("%.2f", data.promocodeFigure ?: 0.0)
                } Cashback in wallet after recharge.")

                rlAppliedCoupon.visible()

            }

        })


        aetAmount.doAfterTextChanged {
            if (it.toString().isBlank()) {
                atvLabelNote.gone()

            } else if (it.toString().startsWith("0")) {
                aetAmount.setText("")

            } else if (it.toString().length > 1) {
                val amt = aetAmount.text.toString().toLong()
                if (amt > 0 && amt % 50 == 0L) {
                    atvLabelNote.gone()
                } else {
                    atvLabelNote.visible()
                }

                if (isAmountSelectedFromCards) {
                    isAmountSelectedFromCards = false

                } else {
                    mOfferAmountList.forEach { obj ->
//                    obj.isSelected = (amt.toDouble() == obj.cardAmount)
                        obj.isSelected = false
                    }

                    mOfferAdapter.notifyDataSetChanged()
                }

            }
        }


        aivBack.setOnClickListener(this)
        atvApplyVoucher.setOnClickListener(this)
        aivRemoveCoupon.setOnClickListener(this)
        atvProceed.setOnClickListener(this)

    }


    private fun setWalletBalance() {
        atvBalance.text = getString(R.string.currency)
        atvBalance.append(getWalletBalance())
    }


    private fun getUpdatedWalletBalance(showProgress: Boolean = false) {
        viewModel?.getWalletBalance(showProgress)
    }


    private fun setUpAdapter() {
        mOfferAdapter = WalletOfferAdapter(mOfferAmountList, mClickListener = { position ->
            // handle click here
            isAmountSelectedFromCards = true

            aetAmount.setText((mOfferAmountList[position].cardAmount ?: 0).toInt().toString())
            aetAmount.setSelection(aetAmount.length())

            mOfferAmountList.forEach { obj ->
                obj.isSelected = false
            }

            mOfferAmountList[position].isSelected = true

            mOfferAdapter.notifyDataSetChanged()

        })

        rvOffers.itemAnimator = DefaultItemAnimator()
        rvOffers.adapter = mOfferAdapter

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvApplyVoucher -> {
                if (isAmountValid()) {
                    if (voucherDialog == null) {
                        voucherDialog = ApplyVoucherCodeDialog(this,
                            applyButtonClick = { voucherCode ->
                                voucherDialog?.hideMessage()
                                if (voucherCode.isBlank()) {
                                    voucherDialog?.showMessage(getString(R.string.enter_voucher_code))

                                } else {

                                    viewModel?.authenticateVoucher(
                                        voucherCode,
                                        VoucherCategory.ADD_MONEY.value,
                                        aetAmount.text.toString()
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
            }


            R.id.atvProceed -> {
                if (isAmountValid()) {
                    val intent = Intent(this, PaymentInformationActivity::class.java)
                    intent.putExtra(AppConstants.KEY_AMOUNT, aetAmount.text.toString().toDouble())
                    if (voucherData != null) {
                        intent.putExtra(AppConstants.KEY_VOUCHER_DATA, voucherData)
                    }
                    startActivityForResult(intent, AppConstants.PAYMENT_INFO_REQUEST_CODE)
                }
            }
        }
    }


    fun isAmountValid(): Boolean {
        return if (aetAmount.text.toString().isBlank()) {
            showShortToast(getString(R.string.please_enter_amount))
            false
        } else if (aetAmount.text.toString().toLong() > 0 &&
            aetAmount.text.toString().toLong() % 50 != 0L
        ) {
            atvLabelNote.visible()
            false
        } else {
            true
        }
    }


    private fun removeVoucher() {
        voucherData = null
        rlAppliedCoupon.gone()
        atvApplyVoucher.visible()
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
            AppConstants.PAYMENT_INFO_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    aetAmount.setText("")
                    removeVoucher()
                    getUpdatedWalletBalance(true)
                }
            }

        }
    }


    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }


}