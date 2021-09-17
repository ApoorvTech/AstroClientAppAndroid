package com.myastrotell.pojo.response.userchatstatus

data class ChatStatusResposne(
    var chatId: String?,
    var chatStartTime: Long?,
    var firstName: String?,
    var lastName: String?,
    var msisdn: String?,
    var profilePicUrl: String?
)