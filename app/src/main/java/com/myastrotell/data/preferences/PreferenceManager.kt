package com.myastrotell.data.preferences

import android.content.Context
import android.content.SharedPreferences


object PreferenceManager {

    private var mPreference: SharedPreferences? = null

    /**
     * =========================
     * Preference Key Constants
     * =========================
     */
    const val IS_LOGGED_IN = "IS_LOGGED_IN"
    const val IS_GUEST_USER = "IS_GUEST_USER"
    const val ARE_TUTORIALS_SHOWN = "ARE_TUTORIALS_SHOWN"
    const val ACCESS_TOKEN = "ACCESS_TOKEN"
    const val DEVICE_ID = "DEVICE_ID"
    const val DEVICE_TOKEN = "DEVICE_TOKEN"
    const val SELECTED_LOCALE = "SELECTED_LOCALE"
    const val MSISDN = "MSISDN"
    const val FIRST_NAME = "FIRST_NAME"
    const val LAST_NAME = "LAST_NAME"
    const val PROFILE_IMAGE = "PROFILE_IMAGE"
    const val WALLET_BALANCE = "WALLET_BALANCE"
    const val FCM_ID = "FCM_ID"
    const val CHAT_START_TIME = "CHAT_START_TIME"
    const val IS_USER_BUSY = "IS_USER_BUSY"


    /**
     * method to initialize [SharedPreferences]
     */
    fun initialize(context: Context) {
        if (mPreference == null) {
            synchronized(this) {
                if (mPreference == null)
                    mPreference =
                        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            }
        }
    }


    fun saveString(key: String, value: String) {
        val editor = mPreference?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun getString(key: String): String {
        return mPreference?.getString(key, "").toString()
    }


    fun saveInt(key: String, value: Int) {
        val editor = mPreference?.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    fun getInt(key: String): Int {
        return mPreference?.getInt(key, 0)!!
    }


    fun saveBoolean(key: String, value: Boolean) {
        val editor = mPreference?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun getBoolean(key: String): Boolean {
        return mPreference?.getBoolean(key, false)!!
    }


    fun clearAllPref() {
        val editor = mPreference?.edit()
        editor?.clear()
        editor?.apply()
    }

}