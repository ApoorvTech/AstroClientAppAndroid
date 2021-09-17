package com.myastrotell.pojo.requests

import com.myastrotell.BuildConfig

data class StartChatRequest(
    val from: String,
    val to: String,
    val appName: String = BuildConfig.APP_NAME
)

data class SendChatRequest(
    val from: String,
    val to: String,
    val chatId: String,
    val message: String,
    val appName: String = BuildConfig.APP_NAME
)

data class EndChatRequest(
    val from: String,
    val to: String,
    val chatId: String,
    val appName: String = BuildConfig.APP_NAME
)

data class NewChatsRequest(val to: String, val appName: String = BuildConfig.APP_NAME)