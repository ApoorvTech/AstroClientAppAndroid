package com.myastrotell.pojo.requests

import com.myastrotell.BuildConfig

data class AppUpdateRequest(
    var appName: String? = "userapp",
    var channel: String? = BuildConfig.CHANNEL,
    var packageName: String?  = BuildConfig.APP_NAME,
    var versionCode: Int? = BuildConfig.VERSION_CODE
)