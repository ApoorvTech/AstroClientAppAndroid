package com.myastrotell.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "userprofile")
class UserProfileEntity {

    @PrimaryKey
    var msisdn: String = ""

    var firstName: String? = ""

    var lastName: String? = ""

    var gender: String? = ""
    var email: String?="" //newly added

    var dateOfBirth: String? = ""

    var timeOfBirth: String? = ""

    var placeOfBirth: String? = ""

    var state: String? = ""

    var city: String? = ""

    var country: String? = ""

    var maritalStatus: String? = ""

    var occupation: String? = ""

    var topicOfConcern: String? = ""

    var profilePicUrl: String? = ""

}