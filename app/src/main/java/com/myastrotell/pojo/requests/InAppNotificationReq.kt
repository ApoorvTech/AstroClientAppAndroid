package com.myastrotell.pojo.requests

import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.BuildConfig


data class InAppNotificationReq(
    val adhoc: Boolean = true,
    val appName: String = BuildConfig.APP_NAME,
    val deviceId: String = DataManager.getStringFromPreference(PreferenceManager.DEVICE_ID),
    val msisdn: String = DataManager.getStringFromPreference(PreferenceManager.MSISDN)
)