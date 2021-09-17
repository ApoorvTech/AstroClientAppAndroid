package com.myastrotell.ui.wallet.paymentinformation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityPaymentInformationBinding
import com.myastrotell.pojo.response.AuthenticateVoucherResponse
import com.myastrotell.pojo.response.OfferListResponse
import com.myastrotell.ui.payments.paymentgateway.PaymentActivity
import com.myastrotell.ui.wallet.WalletViewModel
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_payment_information.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class PaymentInformationActivity :
    BaseActivity<ActivityPaymentInformationBinding, WalletViewModel>(),
    View.OnClickListener {

    private var amount: Double = 0.0
    private var tax: Double = 0.0
    private var totalAmount: Double = 0.0

    private var voucherData: AuthenticateVoucherResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle?.text = getString(R.string.payment_information)

        intent?.let {
            amount = it.getDoubleExtra(AppConstants.KEY_AMOUNT, 0.0)

            voucherData = it.getParcelableExtra(AppConstants.KEY_VOUCHER_DATA)

        }


        viewModel?.getPaymentSummary(amount.toString())

    }


    override fun getLayoutId() = R.layout.activity_payment_information

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<WalletViewModel>()

    override fun initVariables() {

    }

    override fun setObservers() {
        viewModel?.paymentSummaryLiveData?.observe(this, Observer {
            hideProgressBar()

            clMain.visible()

            it?.data?.let { paymentSummary ->
                updateAmount(paymentSummary)
            }

        })

        aivBack.setOnClickListener(this)
        atvApplyCoupon.setOnClickListener(this)
        aivRemoveCoupon.setOnClickListener(this)
        atvProceed.setOnClickListener(this)
    }


    private fun updateAmount(paymentData: OfferListResponse?) {
        paymentData?.let { paymentSummary ->
            amount = paymentSummary.cardAmount ?: 0.0
            tax = paymentSummary.gstAmount ?: 0.0

            val discount = paymentSummary.discountAmount ?: 0.0

            totalAmount = paymentSummary.finalAmtWithGst ?: 0.0


            atvSubtotal.text = ("₹${String.format("%.2f", amount)}")

            if (discount > 0) {
                atvDiscount.text = ("- ₹${String.format("%.2f", discount)}")
                atvLabelDiscount.visible()
                atvDiscount.visible()
            } else {
                atvLabelDiscount.gone()
                atvDiscount.gone()
            }

            atvLabelGST.text = ("${getString(R.string.GST_at)} ${paymentSummary.gstPercent ?: 0.0}%")

            atvGST.text = ("₹${String.format("%.2f", tax)}")

            atvTotalAmount.text = ("₹${String.format("%.2f", totalAmount)}")

        }

        // setting voucher details, if applied.
        voucherData?.let { data ->

            atvCouponTitle.text = data.promocode

            atvCouponMessage.text = ("₹${
                String.format("%.2f", data.promocodeFigure ?: 0.0)
            } Cashback in wallet after recharge.")

            rlAppliedCoupon.visible()
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvApplyCoupon -> {
                atvApplyCoupon.gone()
                rlAppliedCoupon.visible()
            }

            R.id.aivRemoveCoupon -> {
                rlAppliedCoupon.gone()
                atvApplyCoupon.visible()
            }

            R.id.atvProceed -> {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra(AppConstants.KEY_AMOUNT, amount.toString())
                intent.putExtra(AppConstants.KEY_TOTAL_AMOUNT, totalAmount.toString())
                voucherData?.let { data ->
                    intent.putExtra(AppConstants.KEY_VOUCHER_TRANSACTION_ID, data.voucher_transaction_id)
                }
                startActivityForResult(intent, AppConstants.INIT_PAYMENT_REQUEST_CODE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.INIT_PAYMENT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    setResult(RESULT_OK)
                    finish()
                }
            }

        }
    }

}