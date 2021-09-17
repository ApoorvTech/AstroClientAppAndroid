package com.myastrotell.ui.payments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.myastrotell.R
import com.myastrotell.ui.payments.interfaces.Communicator


class ActionDialog :DialogFragment(){
    var communicator: Communicator? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        communicator = activity as Communicator?
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Option")
        builder.setItems(
            R.array.selectAction,
            DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    communicator!!.actionSelected("ResendOTP")
                } else if (which == 1) {
                    communicator!!.actionSelected("EnterOTPManually")
                }
            })
        builder.setNegativeButton(R.string.cancel,
            DialogInterface.OnClickListener { dialog, which -> communicator!!.actionSelected("Cancel") })

        /*builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/
        return builder.create()
    }

}