package com.myastrotell.ui.payments.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log


class SmsReceiver:BroadcastReceiver(){

    private val sms = SmsManager.getDefault();
    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent!!.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle["pdus"] as Array<Any>?
                for (i in pdusObj!!.indices) {
                    val currentMessage: SmsMessage =
                        SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val phoneNumber: String = currentMessage.displayOriginatingAddress
                    val message: String = currentMessage.displayMessageBody

                    //if(senderNum.contains("SBIACS")){
                    val bankOtp =
                        message.replaceFirst(".*?(\\d{6,8}).*".toRegex(), "$1")
                    //}else{
                    // else part.
                    //}
                    val `in` = Intent("SmsMessage.intent.MAIN").putExtra(
                        "get_otp",
                        "$bankOtp|$phoneNumber"
                    )
                    context!!.sendBroadcast(`in`)
                    Log.v("SmsReceiver", "senderNum: $phoneNumber; Banks OTP  $bankOtp")


                    // Show Alert
                    /*int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: "+ senderNum + ", OTP: " + bankOtp, duration);
                    toast.show();*/
                } // end for loop
            }
            //Toast.makeText(context, "SMS Recieved", Toast.LENGTH_SHORT).show();
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}