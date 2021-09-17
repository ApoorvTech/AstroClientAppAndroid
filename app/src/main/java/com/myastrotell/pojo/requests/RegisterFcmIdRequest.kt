package com.myastrotell.pojo.requests

import com.myastrotell.BuildConfig
import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager


class RegisterFcmIdRequest (
    val gcmId: String,
    val msisdn: String = DataManager.getStringFromPreference(PreferenceManager.MSISDN),
    val deviceId: String = DataManager.getStringFromPreference(PreferenceManager.DEVICE_ID),
    val appName: String = BuildConfig.APP_NAME,
    val appVersionCode: Int = BuildConfig.VERSION_CODE,
    val appVersionName: String = BuildConfig.VERSION_NAME,
    val loginLogoutFlag: Boolean = true
)