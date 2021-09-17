package com.myastrotell.pojo.requests

import com.myastrotell.data.Mode

data class SendOtpForResetPassword(val userName: String, val mode: String = Mode.MOBILE.value)