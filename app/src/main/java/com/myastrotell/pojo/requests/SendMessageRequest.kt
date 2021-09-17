package com.myastrotell.pojo.requests

import com.myastrotell.BuildConfig
import com.myastrotell.data.UserRole

data class SendMessageRequest(
    var appName: String = BuildConfig.APP_NAME,
    var chatId: String,
    var to: String,
    var from: String,
    var message: String,
    var isFirstMessage :Boolean = false,
    var userRole :String = UserRole.ROLE_CLIENT.value
)