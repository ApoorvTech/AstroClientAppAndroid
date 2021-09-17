package com.myastrotell.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken


/**
 * used to save/get list in a column in the table
 */
class ListConverters {

    @TypeConverter
    fun fromStringComments(value: String?): List<String?>? {
        val listType = object : TypeToken<List<String?>?>() {
        }.type
        return Gson().fromJson<List<String?>>(value, listType)
    }

    @TypeConverter
    fun fromListComments(list: List<String?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

}