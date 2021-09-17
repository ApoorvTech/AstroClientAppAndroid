package com.myastrotell.pojo.response


data class StartChatResponse(val chatId: String?, val message: String? = null)


data class MessageResponse(
    var appName: String,
    var chatId: String,
    var to: String,
    var from: String,
    var message: String,
    var messageTime: Long,
    var userRole: String?,
    var chatEnd: Boolean?
)