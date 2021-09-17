package com.myastrotell.ui.payments

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.AvenueParams
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.AppUtils.randInt
import com.myastrotell.utils.encryption.Encryption
import kotlinx.coroutines.launch

class WebViewActivityViewModel :BaseViewModel(){
    private lateinit var amount: String
    private val keyData=MutableLiveData<String>()
    private val encryptedValue=MutableLiveData<String>()
    val merchantId="262843"
    val accessCode="AVEH92HF99BW49HEWB"
    var orderIdForPayment=""
    var redirectUrl="http://122.182.6.216/merchant/ccavResponseHandler.jsp"
    var cancelUrl="http://122.182.6.216/merchant/ccavResponseHandler.jsp"

    fun setIntent(intent: Intent?) {
        intent?.let {
           amount= it.getStringExtra(AppConstants.KEY_AMOUNT)?:"0"
        }
    }

    fun getKeyLiveData():LiveData<String>{
        return keyData
    }
    fun getEncryptedValue():LiveData<String>{
        return encryptedValue
    }

    fun getOrderId():String{
        orderIdForPayment= randInt(0, 9999999).toString()
        return orderIdForPayment
    }

    fun getRsaKey(){
        //key=98DA248EFA8BCB46AC112D09EA323255
        //mid=262843
        val map=HashMap<String,String>()
        map[AvenueParams.ACCESS_CODE] = accessCode
        map[AvenueParams.ORDER_ID] = getOrderId()

        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()
            viewModelScope.launch {
                WebViewActivityRepo.getRsaKey(map, ApiRequestCodes.ORDER_DETAILS, keyData, errorLiveData)

            }
        } else {
            navigator?.showNoNetworkError()
        }

    }

    fun handleKey(key: String) {
        viewModelScope.launch {
            encryptAndPass(key)
        }

    }
    private  fun encryptAndPass(key:String){

        val value=StringBuffer("")
        value.append(AppUtils.addToPostParams(AvenueParams.AMOUNT, amount))
        value.append(AppUtils.addToPostParams(AvenueParams.CURRENCY, AppConstants.CURRENCY))
        val  encVal = Encryption.rsaEncrypt(value.substring(0, value.length - 1), key)  //encrypt amount and currency
        encVal?.let {
            encryptedValue.value=it
        }


    }


}