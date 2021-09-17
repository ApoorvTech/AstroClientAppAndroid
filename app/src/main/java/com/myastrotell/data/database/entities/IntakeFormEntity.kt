package com.myastrotell.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intakeform")
class IntakeFormEntity {

    @PrimaryKey
    var mobileNumber: String = ""

    var firstName: String? = ""

    var lastName: String? = ""

    var gender: String? = ""

    var dateOfBirth: String? = ""

    var timeOfBirth: String? = ""

    var placeOfBirth: String? = ""

    var state: String? = ""

    var city: String? = ""

    var country: String? = ""

    var maritalStatus: String? = ""

    var occupation: String? = ""

    var topicOfConcern: String? = ""

    var comment: String? = ""


    var isPartnerSelected: Boolean = false


    var partnerFirstName: String? = ""

    var partnerlastName: String? = ""

    var partnerDateOfBirth: String? = ""

    var partnerTimeOfBirth: String? = ""

    var partnerPlaceOfBirth: String? = ""

    var partnerState: String? = ""

    var partnerCity: String? = ""

    var partnerCountry: String? = ""

}