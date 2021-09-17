package com.myastrotell.interfaces

interface SavedAddressItemClickListener {
    fun onEditClicked(position: Int)
    fun onDeleteClicked(position: Int)
    fun onItemSelected(position: Int)

}