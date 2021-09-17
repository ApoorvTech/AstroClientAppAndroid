package com.myastrotell.interfaces

interface CartItemClickListener {
    fun onMinusClicked(position: Int)
    fun onPlusClicked(position: Int)
}