package com.myastrotell.pojo.requests

import com.myastrotell.data.Mode
import com.myastrotell.data.UserRole


data class LoginRequest(
    val userName: String,
    val password: String,
    val mode: String = Mode.PASSWORD.value,
    val source: String = UserRole.ROLE_CLIENT.value
)