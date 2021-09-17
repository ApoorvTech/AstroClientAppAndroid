package com.myastrotell.ui.payments.interfaces

interface Communicator {
    fun respond(data:String)
    fun actionSelected(data:String)
}