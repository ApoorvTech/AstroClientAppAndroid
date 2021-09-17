package com.myastrotell.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.myastrotell.R
import com.myastrotell.utils.invisible
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.dialog_enter_voucher_code.*


class ApplyVoucherCodeDialog(
    context: Context,
    private val applyButtonClick: (voucherCode: String) -> Unit,
    private val cancelButtonClick: (() -> Unit)? = null
) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_enter_voucher_code)

        setCancelable(false)
        setCanceledOnTouchOutside(false)

        window?.setDimAmount(0.5f)
        window?.setBackgroundDrawable(null)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )


        atvApplyVoucher.setOnClickListener {
            applyButtonClick.invoke(aetVoucherCode.text.toString())
        }

        aivCancel.setOnClickListener {
            dismiss()
            cancelButtonClick?.invoke()
        }

    }


    fun getEnteredCoupon() = aetVoucherCode.text.toString()


    fun reset() {
        aetVoucherCode.setText("")
        hideMessage()
    }

    fun showMessage(message: String?) {
        atvLabelError.text = message.toString()
        atvLabelError.visible()
    }

    fun hideMessage() {
        atvLabelError.text = ""
        atvLabelError.invisible()
    }


}