package com.myastrotell.pojo.response


data class LoginResponse(
    var accessToken: String?,
    var userDetails: UserDetails?,
    var userType: Int?
)

data class UserDetails(
    var firstName: String?,
    var lastName: String?,
    var profileCreationDate: String?,
    var userCategory: String?
)