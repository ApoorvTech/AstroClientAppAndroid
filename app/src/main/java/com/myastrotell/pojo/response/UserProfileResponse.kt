package com.myastrotell.pojo.response

import com.myastrotell.data.ProfileFieldColumn
import com.myastrotell.data.UserRole
import com.myastrotell.pojo.requests.ProfileDetailData


data class UserProfileResponse(val profileDataList: ArrayList<ProfileDetailData>)


data class UpdateProfileRequest(var data: ArrayList<ProfileDetailData>)


class UpdateProfileModel() {

    val mobileNumber by lazy {
        ProfileDetailData(
            "Mobile No.", 1, 1, "TEXT", "",
            ProfileFieldColumn.MSISDN.value, null, 1, "API", 10, 10
        )
    }
    val userName by lazy {
        ProfileDetailData(
            "Username", 1, 1, "TEXT", "",
            ProfileFieldColumn.USERNAME.value, null, 1, "API", 10, 10
        )
    }
    val userCategory by lazy {
        ProfileDetailData(
            "User Category", 1, 1, "TEXT", UserRole.ROLE_CLIENT.value,
            ProfileFieldColumn.USER_CATEGORY.value, null, 4, "API", 5, 20
        )
    }
    val firstName by lazy {
        ProfileDetailData(
            "First Name", 0, 1, "TEXT", "",
            ProfileFieldColumn.FIRST_NAME.value, null, 7, "API", 0, 50
        )
    }
    val lastName by lazy {
        ProfileDetailData(
            "Last Name", 0, 1, "TEXT", "",
            ProfileFieldColumn.LAST_NAME.value, null, 7, "API", 0, 50
        )
    }

    val gender by lazy {
        ProfileDetailData(
            "Gender", 1, 1, "TEXT", "",
            ProfileFieldColumn.GENDER.value, null, 7, "API", 0, 20
        )
    }
    val dob by lazy {
        ProfileDetailData(
            "DOB", 0, 1, "TEXT", "",
            ProfileFieldColumn.DOB.value, null, 7, "API", 0, 20
        )
    }
    val birthPlace by lazy {
        ProfileDetailData(
            "Birth Place", 0, 1, "TEXT", "",
            ProfileFieldColumn.BIRTH_PLACE_STORE_NAME.value, null, 7, "API", 0, 250
        )
    }
    val city by lazy {
        ProfileDetailData(
            "City", 0, 1, "TEXT", "",
            ProfileFieldColumn.CITY.value, null, 7, "API", 0, 50
        )
    }

    val state by lazy {
        ProfileDetailData(
            "State", 0, 1, "TEXT", "",
            ProfileFieldColumn.STATE.value, null, 7, "API", 0, 50
        )
    }
    val country by lazy {
        ProfileDetailData(
            "Country", 0, 1, "TEXT", "",
            ProfileFieldColumn.COUNTRY.value, null, 7, "API", 0, 50
        )
    }
    val maritalStatus by lazy {
        ProfileDetailData(
            "Marital Status", 0, 1, "TEXT", "",
            ProfileFieldColumn.MARITAL_STATUS.value, null, 7, "API", 0, 20
        )
    }
    val occupation by lazy {
        ProfileDetailData(
            "Occupation", 0, 1, "TEXT", "",
            ProfileFieldColumn.OCCUPATION.value, null, 7, "API", 0, 250
        )
    }
    val profilePicUrl by lazy {
        ProfileDetailData(
            "ProfilePicUrl", 0, 1, "TEXT", "",
            ProfileFieldColumn.PROFILE_PIC_URL.value, null, 7, "API", 0, 250
        )
    }

    val email by lazy {
        ProfileDetailData(
            "EmailId", 0, 1, "TEXT", "",
            ProfileFieldColumn.EMAIL.value, null, 7, "API", 1, 100
        )
    }

}
