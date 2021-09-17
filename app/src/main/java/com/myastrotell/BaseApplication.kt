package com.myastrotell

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.drawee.backends.pipeline.Fresco
import com.myastrotell.data.CapturedEvents
import com.myastrotell.data.CustomEvents
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.pojo.response.RedemptionSearchResponse
import com.myastrotell.utils.encryption.Encryption
import com.trackier.sdk.TrackierEvent
import com.trackier.sdk.TrackierSDK
import com.trackier.sdk.TrackierSDKConfig
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes


class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        PreferenceManager.initialize(this)
        initializeTrackier()
        Fresco.initialize(this)
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }


    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }

    private fun initializeTrackier(){
        val TR_DEV_KEY: String = Encryption.getTrackierSDKId()

        Log.e("key",TR_DEV_KEY)



        val context: Context =applicationContext()

      // val sdkConfig = TrackierSDKConfig(this, TR_DEV_KEY, "production")
//        val apkAttributes = AttributionParams("kFyW2bEizc", subSiteID= "sub_partner_tiktok", siteId = "google")
//        sdkConfig.setAttributionParams(apkAttributes)
//        TrackierSDK.initialize(sdkConfig)

        val sdkConfig = TrackierSDKConfig(this, TR_DEV_KEY, "production")

       // val apkAttributes = AttributionParams("kFyW2bEizc", subSiteID= "sub_partner_tiktok", siteId = "google")
        //sdkConfig.setAttributionParams(apkAttributes)
//        sdkConfig.setManualMode(true)
//        TrackierSDK.setLocalRefTrack(true,"_")

        if(PreferenceManager.getBoolean(PreferenceManager.IS_LOGGED_IN)) {
           // TrackierSDK.setUserId("pppppp")
            //TrackierSDK.setUserEmail("abc@gmail.com")
            val userAdditionalDetails: MutableMap<String, Any> = mutableMapOf()
            userAdditionalDetails.put("userMobile", PreferenceManager.getString(PreferenceManager.MSISDN))
            TrackierSDK.setUserAdditionalDetails(userAdditionalDetails)
        }
        TrackierSDK.initialize(sdkConfig)

    }



    fun captureRechargeEvent(status:Int,amount:String,phone:String) {



        val event = TrackierEvent(TrackierEvent.PURCHASE)
        event.revenue=amount.toDouble()

        val customEventMap=HashMap<String,Any>()

        customEventMap[CapturedEvents.AMOUNT] = amount
        customEventMap[CapturedEvents.PHONE] = phone

        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        when(status){
            CapturedEvents.TRANSACTION_STATUS.CANCEL_BY_USER->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.CANCEL
            }
            CapturedEvents.TRANSACTION_STATUS.SUCCESS->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.SUCCES
            }
            CapturedEvents.TRANSACTION_STATUS.FAIL->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.FAIL
            }
            else ->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.UNKNOWN
            }


        }

        event.ev=customEventMap
        TrackierSDK.trackEvent(event)

    }



    /*
    capturing recharge event
     */
     fun capturePurchaseEvent(status:Int,amount:String,phone:String) {

        var eventId=TrackierEvent.PURCHASE
        val moneySpent=amount.toDouble()

        if(moneySpent in 50.0..100.0){
            eventId=CustomEvents.Recharge50

        }
        else if(moneySpent>100 && moneySpent<=200){
            eventId=CustomEvents.Recharge100
        }
        else if(moneySpent>200 && moneySpent<=300){
            eventId=CustomEvents.Recharge200
        }
        else if(moneySpent>300 && moneySpent<=500){
            eventId=CustomEvents.Recharge300
        }
        else if(moneySpent>500 && moneySpent<=700){
            eventId=CustomEvents.Recharge500
        }
        else if(moneySpent>700 && moneySpent<=1000){
            eventId=CustomEvents.Recharge700
        }
        else if(moneySpent>1000 && moneySpent<=1500){
            eventId=CustomEvents.Recharge1000
        }
        else if(moneySpent>1500){
            eventId=CustomEvents.Recharge1500
        }



        val event = TrackierEvent(eventId)



        val customEventMap=HashMap<String,Any>()

        customEventMap[CapturedEvents.AMOUNT] = amount
        customEventMap[CapturedEvents.PHONE] = phone
        event.revenue=amount.toDouble()

        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        when(status){
            CapturedEvents.TRANSACTION_STATUS.CANCEL_BY_USER->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.CANCEL
            }
            CapturedEvents.TRANSACTION_STATUS.SUCCESS->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.SUCCES
            }
            CapturedEvents.TRANSACTION_STATUS.FAIL->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.FAIL
            }
            else ->{
                customEventMap[CapturedEvents.STATUS] = CapturedEvents.UNKNOWN
            }


        }

        event.ev=customEventMap
        TrackierSDK.trackEvent(event)

    }


    fun captureContentViewEvent(tabClicked:String,login:String,phone:String){
        val event = TrackierEvent(TrackierEvent.CONTENT_VIEW)
        val customEventMap=HashMap<String,Any>()
        customEventMap[CapturedEvents.TAB_CLICKED] = tabClicked
        customEventMap[CapturedEvents.PHONE] = phone
        customEventMap[CapturedEvents.LOGIN_STATUS] = login
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)
    }


    fun captureOrderHistoryEvent(tabClicked:String,login:String,phone:String){
        val event = TrackierEvent(CustomEvents.OrderHistory)
        val customEventMap=HashMap<String,Any>()
        customEventMap[CapturedEvents.TAB_CLICKED] = tabClicked
        customEventMap[CapturedEvents.PHONE] = phone
        customEventMap[CapturedEvents.LOGIN_STATUS] = login
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)
    }


    fun captureLoginEvent(status: Boolean, number: String) {
        val event = TrackierEvent(TrackierEvent.LOGIN)
        val customEventMap=HashMap<String,Any>()

        customEventMap[CapturedEvents.PHONE] = number
        if(status)
        customEventMap[CapturedEvents.LOGIN_STATUS] = CapturedEvents.SUCCES
        else
        customEventMap[CapturedEvents.LOGIN_STATUS] = CapturedEvents.FAIL

        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)

    }
    fun captureSignupEvent(number: String) {
        val event = TrackierEvent(CustomEvents.SignUpEvent)

        val customEventMap=HashMap<String,Any>()
        customEventMap[CapturedEvents.PHONE] = number
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)

    }

    fun captureLogoutEvent(eventId:String,phone:String){
        val event = TrackierEvent(eventId)

        val customEventMap= java.util.HashMap<String, Any>()
        customEventMap[CapturedEvents.PHONE] = phone
        customEventMap[CapturedEvents.DATE] = System.currentTimeMillis().toString()
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM

        event.ev=customEventMap
        TrackierSDK.trackEvent(event)



    }
    fun captureShareEvent(phone:String){
        val event = TrackierEvent(CustomEvents.ShareEvent)
        val customEventMap= java.util.HashMap<String, Any>()
        customEventMap[CapturedEvents.PHONE] = phone
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)
    }
     fun captureAddToCartEvent(item: RedemptionSearchResponse) {
        val event = TrackierEvent(TrackierEvent.ADD_TO_CART)

        val customEventMap=HashMap<String,Any>()
        customEventMap["Product ID"] = item.goodsId
        customEventMap["Product Price"] = item.goodsPrice
        customEventMap["Product Name"] = item.goodsName
        customEventMap["Product Quantity"] = item.goodsQuantity
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)

    }

    fun captureCallChatReportEvent(eventId:String,eventName:String,phone:String,amount:Double,status:String,astroName:String){
        val event = TrackierEvent(eventId)

        val customEventMap= java.util.HashMap<String, Any>()

        customEventMap[CapturedEvents.PHONE] = phone
        customEventMap[CapturedEvents.ASTRO_NAME] = astroName
        customEventMap[CapturedEvents.EVENT] = eventName
        customEventMap[CapturedEvents.AMOUNT] = amount
        customEventMap[CapturedEvents.STATUS] = status
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM

        event.ev=customEventMap
        TrackierSDK.trackEvent(event)



    }


    private fun captureEvent(firstName: String,lastName:String,phone: String) {
        val event = TrackierEvent(TrackierEvent.COMPLETE_REGISTRATION)
        val customEventMap=HashMap<String,Any>()
        customEventMap["Name"] = "$firstName $lastName"
        customEventMap["Phone Number"] = phone
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)

    }


}