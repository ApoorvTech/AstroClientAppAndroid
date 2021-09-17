package com.myastrotell.pojo.requests

import com.myastrotell.data.ProfileFieldColumn
import com.myastrotell.data.UserRole

data class IntakeFormRequest(
    var data: ArrayList<IntakeFormFieldModel>,
    var requestType: String? = "temporary"
)

data class IntakeFormFieldModel(
    var displayTitle: String?,
    var isMandatory: Int?,
    var isEditable: Int?,
    var controlType: String?,
    var fieldValue: String?,
    var fieldColumnName: String?,
    var dataValues: Any?,
    var sequence: Int?,
    var mode: String?,
    var minLength: Int?,
    var maxLength: Int?
)

class FieldModel() {

    val mobileNumber by lazy {
        IntakeFormFieldModel(
            "Mobile No.", 1, 1, "TEXT", "",
            ProfileFieldColumn.MSISDN.value, null, 1, "API", 10, 10
        )
    }
    val userName by lazy {
        IntakeFormFieldModel(
            "Username", 1, 1, "TEXT", "",
            ProfileFieldColumn.USERNAME.value, null, 1, "API", 10, 10
        )
    }
    val userCategory by lazy {
        IntakeFormFieldModel(
            "User Category", 1, 1, "TEXT", UserRole.ROLE_CLIENT.value,
            ProfileFieldColumn.USER_CATEGORY.value, null, 4, "API", 5, 20
        )
    }
    val firstName by lazy {
        IntakeFormFieldModel(
            "First Name", 1, 1, "TEXT", "",
            ProfileFieldColumn.FIRST_NAME.value, null, 7, "API", 0, 50
        )
    }
    val lastName by lazy {
        IntakeFormFieldModel(
            "Last Name", 0, 1, "TEXT", "",
            ProfileFieldColumn.LAST_NAME.value, null, 7, "API", 0, 50
        )
    }
    val gender by lazy {
        IntakeFormFieldModel(
            "Gender", 1, 1, "TEXT", "",
            ProfileFieldColumn.GENDER.value, null, 7, "API", 0, 20
        )
    }
    val dob by lazy {
        IntakeFormFieldModel(
            "DOB", 1, 1, "TEXT", "",
            ProfileFieldColumn.DOB.value, null, 7, "API", 0, 20
        )
    }
    val maritalStatus by lazy {
        IntakeFormFieldModel(
            "Marital Status", 0, 1, "TEXT", "",
            ProfileFieldColumn.MARITAL_STATUS.value, null, 7, "API", 0, 20
        )
    }
    val occupation by lazy {
        IntakeFormFieldModel(
            "Occupation", 0, 1, "TEXT", "",
            ProfileFieldColumn.OCCUPATION.value, null, 7, "API", 0, 250
        )
    }
    val birthPlace by lazy {
        IntakeFormFieldModel(
            "Birth Place", 1, 1, "TEXT", "",
            ProfileFieldColumn.BIRTH_PLACE_STORE_NAME.value, null, 7, "API", 0, 250
        )
    }
    val city by lazy {
        IntakeFormFieldModel(
            "City", 0, 1, "TEXT", "",
            ProfileFieldColumn.CITY.value, null, 7, "API", 0, 50
        )
    }

    val state by lazy {
        IntakeFormFieldModel(
            "State", 0, 1, "TEXT", "",
            ProfileFieldColumn.STATE.value, null, 7, "API", 0, 50
        )
    }

    val country by lazy {
        IntakeFormFieldModel(
            "Country", 0, 1, "TEXT", "",
            ProfileFieldColumn.COUNTRY.value, null, 7, "API", 0, 50
        )
    }

    val concern by lazy {
        IntakeFormFieldModel(
            "Short Description", 0, 1, "TEXT", "",
            ProfileFieldColumn.CONCERN_SHORT_DESCRIPTION.value, null, 7, "API", 0, 250
        )
    }

    val language by lazy {
        IntakeFormFieldModel(
            "Languages", 0, 1, "TEXT", "",
            ProfileFieldColumn.PROFILE_LANGUAGE.value, null, 7, "API", 0, 50
        )
    }

    val comment by lazy {
        IntakeFormFieldModel(
            "Description", 0, 1, "TEXT", "",
            ProfileFieldColumn.COMMENT_DESCRIPTION.value, null, 7, "API", 0, 500
        )
    }

    val partnerName by lazy {
        IntakeFormFieldModel(
            "Partner Name", 0, 1, "TEXT", "",
            ProfileFieldColumn.PARTNER_NAME_LEVEL1_NAME.value, null, 7, "API", 0, 100
        )
    }

    val partnerDetails by lazy {
        IntakeFormFieldModel(
            "Partner Details", 0, 1, "TEXT", "",
            ProfileFieldColumn.PARTNER_DETAIL_LEVEL1_ID.value, null, 7, "API", 0, 150
        )
    }

}


