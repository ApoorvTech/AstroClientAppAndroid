package com.myastrotell.pojo.response

data class KeyDataResponse(
    val id: Int,
    val key: String,
    val value: String
) {
     var isSelected = false
}