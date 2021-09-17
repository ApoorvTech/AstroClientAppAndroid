package com.myastrotell.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ChatApiKeys
import com.myastrotell.data.DataManager
import com.myastrotell.data.ProfileFieldColumn
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.data.restapi.ResultWrapper
import com.myastrotell.pojo.requests.ProfileDetailData
import com.myastrotell.pojo.response.HomeDataResponse
import com.myastrotell.pojo.response.HomeStaticDataResponse
import com.myastrotell.pojo.response.UserProfileResponse
import com.myastrotell.pojo.response.userchatstatus.ChatStatusResposne
import com.myastrotell.utils.AppUtils
import kotlinx.coroutines.launch


class HomeViewModel : BaseViewModel() {
    private val mRepo = HomeRepo()

    val homeFeaturesLiveData by lazy { MutableLiveData<BaseResponseModel<HomeDataResponse>>() }
    val homeStaticDataLiveData by lazy { MutableLiveData<BaseResponseModel<List<HomeStaticDataResponse>>>() }
    val chatStatusLiveData by lazy { MutableLiveData<BaseResponseModel<List<ChatStatusResposne>>>() }
    val registerFcmIdLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }

    val mProfileLiveData by lazy { MutableLiveData<BaseResponseModel<UserProfileResponse>>() }

    val endChatResponse by lazy { MutableLiveData<BaseResponseModel<Any>>() }


    fun getFcmID() = mRepo.getFcmID()

    fun getFirstName() = mRepo.getFirstName()

    fun getLastName() = mRepo.getLastName()

    fun getProfileImage() = mRepo.getProfileImage()

    fun getMsisdn() = mRepo.getMsisdn()


    fun getHomeData() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getHomeData(ApiRequestCodes.HOME_DATA, homeFeaturesLiveData, errorLiveData)
                mRepo.getHomeStaticData(ApiRequestCodes.HOME_DATA, homeStaticDataLiveData, errorLiveData)
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun registerFcmId() {
        if (navigator?.isNetworkAvailable() == true) {
            viewModelScope.launch {
                mRepo.registerFcmId(
                    ApiRequestCodes.REGISTER_FCM_ID,
                    registerFcmIdLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun getWalletBalance() {
        if (navigator?.isNetworkAvailable() == true) {

            viewModelScope.launch {
                mRepo.getWalletBalance(ApiRequestCodes.WALLET_BALANCE)
            }

        }
    }


    fun getChatStatusForUser() {
        if (navigator?.isNetworkAvailable() == true) {

            viewModelScope.launch {
                mRepo.getChatStatusForUser(
                    ApiRequestCodes.CHAT_STATUS,
                    chatStatusLiveData,
                    errorLiveData
                )
            }

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

                    ProfileFieldColumn.EMAIL.value -> {
                        entity.email = data.fieldValue
                    }

                    ProfileFieldColumn.GENDER.value -> {
                        entity.gender = data.fieldValue
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

        }
    }


    fun endChat( astrologerNumber: String?, chatID: String?) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val paramMap = HashMap<String, String>()
                paramMap.put(ChatApiKeys.appName, BuildConfig.APP_NAME)
                paramMap.put(ChatApiKeys.from, DataManager.getMsisdn())
                paramMap.put(ChatApiKeys.to, astrologerNumber ?: "")
                paramMap.put(ChatApiKeys.chatId, chatID ?: "")

                mRepo.endChat(
                    ApiRequestCodes.END_CHAT,
                    paramMap
                ).let {
                    when (it) {
                        is ResultWrapper.Success -> {
                            endChatResponse.value = it.response
                        }
                        is ResultWrapper.GenericError -> {
                            errorLiveData.value = it.response
                        }
                    }
                }
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


}