package com.myastrotell.pojo.requests

data class SubmitSupportFormRequest(
    var name: String?,
    var number: String?,
    var email: String?,
    var query: String?
)