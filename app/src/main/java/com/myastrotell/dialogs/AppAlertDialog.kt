package com.myastrotell.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.myastrotell.R
import com.myastrotell.utils.gone
import kotlinx.android.synthetic.main.dialog_alert.*


class AppAlertDialog(
    context: Context,
    @DrawableRes private val icon: Int = 0,
    private val title: String?,
    private val message: String?,
    private val positiveBtnText: String?,
    private val negativeBtnText: String?,
    private val onPositiveBtnClicked: (() -> Unit)?,
    private val onNegativeBtnClicked: (() -> Unit)? = null
) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_alert)

        setCancelable(false)
        setCanceledOnTouchOutside(false)

        window?.setDimAmount(0.5f)
        window?.setBackgroundDrawable(null)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        if (title.isNullOrBlank()) {
            atvTitle.gone()
        } else {
            atvTitle.text = title
            atvTitle.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        }

        if (message.isNullOrBlank()) {
            atvMessage.gone()
        } else {
            atvMessage.text = message
        }


        if (positiveBtnText.isNullOrBlank()) {
            atvPositive.gone()
        } else {
            atvPositive.text = positiveBtnText
        }


        if (negativeBtnText.isNullOrBlank()) {
            atvNegative.gone()
        } else {
            atvNegative.text = negativeBtnText
        }

        atvPositive.setOnClickListener {
            dismiss()
            onPositiveBtnClicked?.invoke()
        }

        atvNegative.setOnClickListener {
            dismiss()
            onNegativeBtnClicked?.invoke()
        }

    }


}