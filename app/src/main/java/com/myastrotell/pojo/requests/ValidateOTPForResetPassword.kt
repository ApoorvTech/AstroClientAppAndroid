package com.myastrotell.pojo.requests

import com.myastrotell.data.Mode

data class ValidateOTPForResetPassword(val userName : String, val otp : String, val mode: String = Mode.MOBILE.value)