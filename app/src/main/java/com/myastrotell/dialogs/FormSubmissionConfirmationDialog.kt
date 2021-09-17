package com.myastrotell.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.myastrotell.R
import kotlinx.android.synthetic.main.dialog_form_submission_confirmation.*


class FormSubmissionConfirmationDialog(
    context: Context,
    private val date: String?,
    private val time: String?,
    private val place: String?,
    private val onPositiveBtnClicked: (() -> Unit)?
) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_form_submission_confirmation)

        setCancelable(false)

        window?.setDimAmount(0.5f)
        window?.setBackgroundDrawable(null)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )


        atvDOB.text = date

        atvTOB.text = time

        atvPOB.text = place


        atvPositive.setOnClickListener {
            dismiss()
            onPositiveBtnClicked?.invoke()
        }

        atvNegative.setOnClickListener {
            dismiss()
        }

    }


}