package com.myastrotell.ui.detailform

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.BuildConfig
import com.myastrotell.R
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.AstrologersListType
import com.myastrotell.data.ChatApiKeys
import com.myastrotell.data.DataManager
import com.myastrotell.data.database.entities.IntakeFormEntity
import com.myastrotell.pojo.DetailFormSelectionModel
import com.myastrotell.pojo.requests.*
import com.myastrotell.pojo.response.CallRequestResponse
import com.myastrotell.pojo.response.StartChatResponse
import com.myastrotell.utils.AppUtils
import kotlinx.coroutines.launch


class DetailsFormViewModel : BaseViewModel() {

    private val mRepo = DetailsFormRepo()

    var type: String = ""


    val intakeFormData by lazy { MutableLiveData<IntakeFormEntity>() }

    val concernData by lazy { MutableLiveData<List<DetailFormSelectionModel>>() }

    val saveIntakeFormResponse by lazy { MutableLiveData<BaseResponseModel<List<IntakeFormFieldModel>>>() }


    val chatRequestLiveData by lazy { MutableLiveData<BaseResponseModel<StartChatResponse>>() }

    val initChatLiveData by lazy { MutableLiveData<BaseResponseModel<StartChatResponse>>() }

    val callRequestLiveData by lazy { MutableLiveData<BaseResponseModel<CallRequestResponse>>() }

    val reportRequestLiveData by lazy { MutableLiveData<BaseResponseModel<Any>>() }


    val validationError by lazy { MutableLiveData<String>() }


    /**
     * method to get saved form data from local db
     */
    fun getFormData() {
        viewModelScope.launch {

            var data = mRepo.getIntakeFormData()

            if (data == null) {
                data = IntakeFormEntity()
                data.mobileNumber = DataManager.getMsisdn()

                val savedProfileData = mRepo.getSavedProfileData()
                if (savedProfileData != null) {
                    data.firstName = savedProfileData.firstName
                    data.lastName = savedProfileData.lastName
                    data.gender = savedProfileData.gender
                    data.dateOfBirth = savedProfileData.dateOfBirth
                    data.timeOfBirth = savedProfileData.timeOfBirth
                    data.placeOfBirth = savedProfileData.placeOfBirth
                    data.state = savedProfileData.state
                    data.city = savedProfileData.city
                    data.country = savedProfileData.country
                    data.maritalStatus = savedProfileData.maritalStatus
                    data.occupation = savedProfileData.occupation
                    data.topicOfConcern = savedProfileData.topicOfConcern
                }

                mRepo.addIntakeFormData(data)
            }

            intakeFormData.value = data
        }
    }


    /**
     * method to clear form data from local db
     */
    fun clearIntakeFormData() {
        viewModelScope.launch {
            mRepo.clearIntakeFormData()
        }
    }


    /**
     * method to get list of topic of concerns
     */
    fun getConcerns() {
        if (navigator?.isNetworkAvailable() == true) {
            viewModelScope.launch {
                mRepo.getConcerns(ApiRequestCodes.GET_KEY_VALUE, concernData, errorLiveData)
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * method to validate form
     */
    fun validateForm(data: IntakeFormEntity, resources: Resources): Boolean {
        if (data.firstName.isNullOrEmpty()) {
            validationError.value = resources.getString(R.string.enterFirstName)
            return false
        } else if (data.gender.isNullOrEmpty()) {
            validationError.value = resources.getString(R.string.selectGender)
            return false
        } else if (data.dateOfBirth.isNullOrEmpty()) {
            validationError.value = resources.getString(R.string.selectDOB)
            return false
        } else if (data.timeOfBirth.isNullOrEmpty()) {
            validationError.value = resources.getString(R.string.selectTOB)
            return false
        } else if (data.placeOfBirth.isNullOrEmpty()) {
            validationError.value = resources.getString(R.string.enterPlaceOfBirth)
            return false
        } else if (type.equals(AstrologersListType.REPORT.value, true) &&
            data.maritalStatus.isNullOrEmpty()
        ) {
            validationError.value = resources.getString(R.string.selectMaritalStatus)
            return false
        } else if (type.equals(AstrologersListType.REPORT.value, true) &&
            data.topicOfConcern.isNullOrEmpty()
        ) {
            validationError.value = resources.getString(R.string.selectTopicOfConcern)
            return false

        } else if (type.equals(AstrologersListType.REPORT.value, true) &&
            data.isPartnerSelected) {

            if (data.partnerFirstName.isNullOrEmpty()) {
                validationError.value = resources.getString(R.string.enterPartnerFirstName)
                return false
            } else if (data.partnerDateOfBirth.isNullOrEmpty()) {
                validationError.value = resources.getString(R.string.enterPartnerDOB)
                return false
            } else if (data.partnerTimeOfBirth.isNullOrEmpty()) {
                validationError.value = resources.getString(R.string.enterPartnerTOB)
                return false
            } else if (data.partnerPlaceOfBirth.isNullOrEmpty()) {
                validationError.value = resources.getString(R.string.enterPartnerPOB)
                return false
            }
        }

        return true

    }


    /**
     * method to save form on Server and local db
     */
    fun saveFormOnServerAndDatabase(data: IntakeFormEntity, language: String) {
        val fieldList = ArrayList<IntakeFormFieldModel>()

        val dateFormat = "dd-MMMM-yyyy hh:mm a"
        val dateOfBirth = "${data.dateOfBirth} ${data.timeOfBirth}"

        val fieldModel = FieldModel()

        fieldList.add(fieldModel.firstName.apply { fieldValue = data.firstName })
        fieldList.add(fieldModel.lastName.apply { fieldValue = data.lastName })
        fieldList.add(fieldModel.mobileNumber.apply { fieldValue = data.mobileNumber })
        fieldList.add(fieldModel.gender.apply { fieldValue = data.gender })
        fieldList.add(fieldModel.dob.apply {
           // fieldValue = AppUtils.dateToTimeStamp(dateOfBirth, dateFormat).toString()
            fieldValue = AppUtils.getTimestampFromStringDate(dateOfBirth, dateFormat).toString()
        })
        fieldList.add(fieldModel.birthPlace.apply { fieldValue = data.placeOfBirth })
        fieldList.add(fieldModel.state.apply { fieldValue = data.state })
        fieldList.add(fieldModel.city.apply { fieldValue = data.city })
        fieldList.add(fieldModel.country.apply { fieldValue = data.country })
        fieldList.add(fieldModel.maritalStatus.apply { fieldValue = data.maritalStatus })
        fieldList.add(fieldModel.occupation.apply { fieldValue = data.occupation })
        fieldList.add(fieldModel.concern.apply { fieldValue = data.topicOfConcern })
        fieldList.add(fieldModel.language.apply { fieldValue = language })
        fieldList.add(fieldModel.comment.apply { fieldValue = data.comment })


        if (data.isPartnerSelected) {
            fieldList.add(fieldModel.partnerName.apply {
                fieldValue = "${data.partnerFirstName} ${data.partnerlastName}"
            })

            val partnerDobStr = "${data.partnerDateOfBirth} ${data.partnerTimeOfBirth}"
            val partnerDateOfBirth = AppUtils.dateToTimeStamp(partnerDobStr, dateFormat)

            fieldList.add(fieldModel.partnerDetails.apply {
                fieldValue =
                    "${partnerDateOfBirth}|${data.partnerPlaceOfBirth}|${data.partnerCity}|${data.partnerState}|${data.partnerCountry}"
            })

        } else {
            fieldList.add(fieldModel.partnerName.apply { fieldValue = "" })
            fieldList.add(fieldModel.partnerDetails.apply { fieldValue = "" })
        }

        fieldList.add(fieldModel.userCategory)
        fieldList.add(fieldModel.userName.apply { fieldValue = data.mobileNumber })

        val request = IntakeFormRequest(data = fieldList)


        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                mRepo.addIntakeFormData(data)

                mRepo.saveIntakeFrom(
                    ApiRequestCodes.SAVE_INTAKE_FORM,
                    request,
                    saveIntakeFormResponse,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }

    }


    // UnUsed
    /**
     * method to send chat request
     */
    fun sendChatRequest(to: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val paramMap = HashMap<String, String>()
                paramMap.put(ChatApiKeys.appName, BuildConfig.APP_NAME)
                paramMap.put(ChatApiKeys.from, DataManager.getMsisdn())
                paramMap.put(ChatApiKeys.to, to)

                mRepo.sendChatRequest(
                    ApiRequestCodes.SEND_CHAT_REQUEST,
                    paramMap,
                    chatRequestLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * method to initialize chat
     */
    fun initChat(to: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val paramMap = HashMap<String, String>()
                paramMap.put(ChatApiKeys.appName, BuildConfig.APP_NAME)
                paramMap.put(ChatApiKeys.from, DataManager.getMsisdn())
                paramMap.put(ChatApiKeys.to, to)

                mRepo.initChat(
                    ApiRequestCodes.START_CHAT,
                    paramMap,
                    initChatLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * method to init call request
     */
    fun initCallRequest(astrologerId: String, price: String, balance: String) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                val callRequest = CallRequest(
                    astrologerId,
                    price,
                    balance,
                    DataManager.getMsisdn()
                )

                mRepo.callRequest(
                    ApiRequestCodes.CALL_REQUEST,
                    callRequest,
                    callRequestLiveData,
                    errorLiveData
                )
            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


    /**
     * method to init order report request
     */
    fun initReportRequest(
        astrologerId: String,
        price: String,
        formData: IntakeFormEntity
    ) {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {

                val name = (formData.firstName + " " + formData.lastName).trim()

                val address = Address(DataManager.getMsisdn(), name)

                val formInfo = StringBuilder()
                formInfo.append(name)
                formInfo.append("|")
                formInfo.append(formData.mobileNumber)
                formInfo.append("|")
                formInfo.append(formData.dateOfBirth)
                formInfo.append(" ")
                formInfo.append(formData.timeOfBirth)
                formInfo.append("|")
                formInfo.append(formData.placeOfBirth)
                formInfo.append("|")
                formInfo.append(formData.maritalStatus)
                formInfo.append("|")
                formInfo.append(formData.occupation)
                formInfo.append("|")
                formInfo.append(formData.topicOfConcern)

                val request = RedeemPointsRequest(
                    address,
                    "en",
                    "2",
                    "0",
                    "0",
                    AstrologersListType.REPORT.value,
                    astrologerId,
                    "whiteGoods",
                    price,
                    "1",
                    price,
                    formInfo.toString()
                )

                mRepo.orderReport(
                    ApiRequestCodes.REDEEM_POINTS,
                    request,
                    reportRequestLiveData,
                    errorLiveData
                )

            }
        } else {
            navigator?.showNoNetworkError()
        }
    }


}