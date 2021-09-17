package com.myastrotell.ui.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.myastrotell.base.BaseRepository
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.DataManager
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.ProfileDetailData
import com.myastrotell.pojo.response.UpdateProfileRequest
import com.myastrotell.pojo.response.UploadFileResponse
import com.myastrotell.pojo.response.UserProfileResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*


class UserProfileRepo : BaseRepository() {

    suspend fun getSavedProfileData(): UserProfileEntity? {
        return DataManager.getProfileData()
    }


    suspend fun saveProfileData(entity: UserProfileEntity) {
        DataManager.saveStringInPreference(PreferenceManager.FIRST_NAME, entity.firstName ?: "")
        DataManager.saveStringInPreference(PreferenceManager.LAST_NAME, entity.lastName ?: "")
        DataManager.saveStringInPreference(
            PreferenceManager.PROFILE_IMAGE, entity.profilePicUrl ?: ""
        )
        // saving in DB
        DataManager.saveProfileData(entity)
    }


    suspend fun getUserProfileDetails(
        apiRequestCode: Int,
        mProfileLiveData: MutableLiveData<BaseResponseModel<UserProfileResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.getUserProfileDetails()

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    mProfileLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


    suspend fun uploadProfileImage(
        apiRequestCode: Int,
        imagePath: String,
        updateProfileLiveData: MutableLiveData<BaseResponseModel<UploadFileResponse>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        val imageUri = Uri.parse(imagePath)

        val arr = imageUri.path!!.split(".")
        val fileExt = arr[arr.size - 1]

        val fileRequestBody =
            File(imageUri.path!!).asRequestBody("image/$fileExt".toMediaTypeOrNull())

        val pathArr = imageUri.path!!.split("/")
        val fileName = pathArr[pathArr.size-1]

        val request = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("uploadCategory", "Profile")
            .addFormDataPart("displayTitle", "Profile")
            .addFormDataPart("uploadFile", fileName, fileRequestBody)
            .build()


        safeApiCall(apiRequestCode) {

            DataManager.uploadProfileImage(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    updateProfileLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }



    suspend fun updateUserProfile(
        apiRequestCode: Int,
        request: UpdateProfileRequest,
        updateProfileLiveData: MutableLiveData<BaseResponseModel<ArrayList<ProfileDetailData>>>,
        errorLiveData: MutableLiveData<BaseResponseModel<*>>
    ) {

        safeApiCall(apiRequestCode) {

            DataManager.updateUserProfile(request)

        }.let {
            when (it) {
                is ResultWrapper.Success -> {
                    updateProfileLiveData.value = it.response
                }

                is ResultWrapper.GenericError -> {
                    errorLiveData.value = it.response
                }
            }
        }

    }


}