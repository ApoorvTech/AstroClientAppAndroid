package com.myastrotell.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.CapturedEvents
import com.myastrotell.data.ProfileFieldColumn
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.pojo.requests.ProfileDetailData
import com.myastrotell.pojo.response.UpdateProfileModel
import com.myastrotell.pojo.response.UpdateProfileRequest
import com.myastrotell.pojo.response.UploadFileResponse
import com.myastrotell.pojo.response.UserProfileResponse
import com.myastrotell.utils.AppUtils
import com.trackier.sdk.TrackierEvent
import com.trackier.sdk.TrackierSDK
import kotlinx.coroutines.launch


class UserProfileViewModel : BaseViewModel() {
    private val mRepo = UserProfileRepo()

    val savedProfileLiveData by lazy { MutableLiveData<UserProfileEntity>() }

    val mProfileLiveData by lazy { MutableLiveData<BaseResponseModel<UserProfileResponse>>() }

    val uploadProfileImageLiveData by lazy { MutableLiveData<BaseResponseModel<UploadFileResponse>>() }

    val updateProfileLiveData by lazy { MutableLiveData<BaseResponseModel<ArrayList<ProfileDetailData>>>() }


    fun getSavedProfileDetails() {
        viewModelScope.launch {
            savedProfileLiveData.value = mRepo.getSavedProfileData()
        }
    }


    fun getProfileDetails() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getUserProfileDetails(
                    ApiRequestCodes.GET_PROFILE_DATA,
                    mProfileLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun uploadProfileImage(imagePath: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                mRepo.uploadProfileImage(
                    ApiRequestCodes.UPLOAD_PROFILE_IMAGE,
                    imagePath,
                    uploadProfileImageLiveData,
                    errorLiveData
                )

            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun updateUserProfile(entity: UserProfileEntity) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            val fieldList = ArrayList<ProfileDetailData>()

            val dateFormat = "dd-MMMM-yyyy hh:mm aa"
            val dateOfBirth = "${entity.dateOfBirth} ${entity.timeOfBirth}"
            val dob = AppUtils.dateToTimeStamp(dateOfBirth, dateFormat).toString()

            val fieldModel = UpdateProfileModel()

            fieldList.add(fieldModel.firstName.apply { fieldValue = entity.firstName })
            fieldList.add(fieldModel.lastName.apply { fieldValue = entity.lastName })
            fieldList.add(fieldModel.mobileNumber.apply { fieldValue = entity.msisdn })

            fieldList.add(fieldModel.email.apply { fieldValue = entity.email })

            fieldList.add(fieldModel.gender.apply { fieldValue = entity.gender })
            fieldList.add(fieldModel.dob.apply { fieldValue = dob })
            fieldList.add(fieldModel.birthPlace.apply { fieldValue = entity.placeOfBirth })
            fieldList.add(fieldModel.state.apply { fieldValue = entity.state })
            fieldList.add(fieldModel.city.apply { fieldValue = entity.city })
            fieldList.add(fieldModel.country.apply { fieldValue = entity.country })
            fieldList.add(fieldModel.maritalStatus.apply { fieldValue = entity.maritalStatus })
            fieldList.add(fieldModel.occupation.apply { fieldValue = entity.occupation })

            fieldList.add(fieldModel.profilePicUrl.apply { fieldValue = entity.profilePicUrl })

            val request = UpdateProfileRequest(fieldList)

            viewModelScope.launch {

                mRepo.updateUserProfile(
                    ApiRequestCodes.UPDATE_PROFILE_DATA,
                    request,
                    updateProfileLiveData,
                    errorLiveData
                )

            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun saveProfileDetailsInDB(profileData: ArrayList<ProfileDetailData>) {
        viewModelScope.launch {
            val entity = UserProfileEntity()

            profileData.forEach { data ->
                when (data.fieldColumnName) {
                    ProfileFieldColumn.FIRST_NAME.value -> {
                        entity.firstName = data.fieldValue
                    }

                    ProfileFieldColumn.LAST_NAME.value -> {
                        entity.lastName = data.fieldValue
                    }

                    ProfileFieldColumn.MSISDN.value -> {
                        entity.msisdn = ("${data.fieldValue}")
                    }

                    ProfileFieldColumn.GENDER.value -> {
                        entity.gender = data.fieldValue
                    }

                    //newly added
                    ProfileFieldColumn.EMAIL.value -> {
                        entity.email = data.fieldValue
                    }

                    ProfileFieldColumn.DOB.value -> {
                        if (data.fieldValue.isNullOrBlank()) {
                            entity.dateOfBirth = ""
                            entity.timeOfBirth = ""
                        } else {
                            val dateTime = AppUtils.timeStampToDate(
                                data.fieldValue.toString().toLong(),
                                "dd-MMMM-yyyy, hh:mm aa"
                            )
                            if (dateTime.isNotBlank()) {
                                val timeArr = dateTime.split(", ")
                                entity.dateOfBirth = timeArr[0]
                                entity.timeOfBirth = timeArr[1]
                            }
                        }
                    }

                    ProfileFieldColumn.BIRTH_PLACE_STORE_NAME.value -> {
                        entity.placeOfBirth = data.fieldValue
                    }

                    ProfileFieldColumn.CITY.value -> {
                        entity.city = data.fieldValue
                    }

                    ProfileFieldColumn.STATE.value -> {
                        entity.state = data.fieldValue
                    }

                    ProfileFieldColumn.COUNTRY.value -> {
                        entity.country = data.fieldValue
                    }

                    ProfileFieldColumn.MARITAL_STATUS.value -> {
                        entity.maritalStatus = data.fieldValue
                    }

                    ProfileFieldColumn.OCCUPATION.value -> {
                        entity.occupation = data.fieldValue
                    }

                    ProfileFieldColumn.CONCERN_SHORT_DESCRIPTION.value -> {
                        entity.topicOfConcern = data.fieldValue
                    }

                    ProfileFieldColumn.PROFILE_PIC_URL.value -> {
                        entity.profilePicUrl = data.fieldValue
                    }
                }
            }

            mRepo.saveProfileData(entity)
            captureEvent(entity)
            savedProfileLiveData.value = entity

        }
    }
    private fun captureEvent(item: UserProfileEntity) {
        val event = TrackierEvent(TrackierEvent.COMPLETE_REGISTRATION)
        val customEventMap=HashMap<String,Any>()
        customEventMap[CapturedEvents.NAME] = item.firstName+" "+item.lastName
        customEventMap[CapturedEvents.PHONE] = item.msisdn
        customEventMap[CapturedEvents.EMAIL] = item.email?:""
        customEventMap[CapturedEvents.LOGIN_PLATFORM] = CapturedEvents.PLATFORM
        event.ev=customEventMap
        TrackierSDK.trackEvent(event)

    }


}