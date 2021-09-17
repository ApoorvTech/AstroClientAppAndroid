package com.myastrotell.ui.detailform

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.myastrotell.BaseApplication
import com.myastrotell.R
import com.myastrotell.adapters.DetailFormSelectionAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.data.AstrologersListType
import com.myastrotell.data.CapturedEvents
import com.myastrotell.data.CustomEvents
import com.myastrotell.data.database.entities.IntakeFormEntity
import com.myastrotell.databinding.ActivityDetailsFormBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.dialogs.FormSubmissionConfirmationDialog
import com.myastrotell.interfaces.DetailFormSelection
import com.myastrotell.pojo.DetailFormSelectionModel
import com.myastrotell.ui.chat.ChatActivity
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import com.trackier.sdk.TrackierEvent
import com.trackier.sdk.TrackierSDK
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_details_form.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailsFormActivity : BaseActivity<ActivityDetailsFormBinding, DetailsFormViewModel>(),
    View.OnClickListener {


    private lateinit var astrologerId: String
    private lateinit var astrologerName: String
    private lateinit var astrologerImageUrl: String
    private var amount: Double = 0.0
    private var type: String = ""

    private lateinit var formData: IntakeFormEntity

    private lateinit var genderList: ArrayList<DetailFormSelectionModel>
    private var genderAdapter: DetailFormSelectionAdapter? = null

    private lateinit var maritalStatusList: ArrayList<DetailFormSelectionModel>
    private var maritalStatusAdapter: DetailFormSelectionAdapter? = null

    private lateinit var languagesList: ArrayList<DetailFormSelectionModel>
    private var languageAdapter: DetailFormSelectionAdapter? = null

    private lateinit var concernsList: ArrayList<DetailFormSelectionModel>
    private var concernsAdapter: DetailFormSelectionAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        viewModel?.getFormData()

        viewModel?.getConcerns()

    }


    override fun getLayoutId() = R.layout.activity_details_form


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<DetailsFormViewModel>()


    override fun initVariables() {
        genderList = ArrayList()
        maritalStatusList = ArrayList()
        languagesList = ArrayList()
        concernsList = ArrayList()
    }


    private fun getData() {
        viewModel?.type =
            intent?.getStringExtra(AppConstants.KEY_TYPE) ?: AstrologersListType.CHAT.value

        astrologerId = intent?.getStringExtra(AppConstants.KEY_ID) ?: ""
        astrologerName = intent?.getStringExtra(AppConstants.KEY_TITLE) ?: ""
        astrologerImageUrl = intent?.getStringExtra(AppConstants.KEY_IMAGE) ?: ""
        amount = intent?.getDoubleExtra(AppConstants.KEY_PRICE, 0.0) ?: 0.0

        when (viewModel?.type) {
            AstrologersListType.CHAT.value -> {
                atvTitle.text = getString(R.string.chat_intake_form)
                btnSubmit.text = getString(R.string.submit)

                tilFirstName.hint = getString(R.string.full_name_mandatory)
                type=AstrologersListType.CHAT.value
               // etFirstName.setHint(getString(R.string.full_name_mandatory))

            }

            AstrologersListType.CALL.value -> {
                atvTitle.text = getString(R.string.call_intake_form)
                btnSubmit.text = getString(R.string.submit)

                tilFirstName.hint = getString(R.string.full_name_mandatory)
                type=AstrologersListType.CALL.value

                // etFirstName.setHint(getString(R.string.full_name_mandatory))

            }

            AstrologersListType.REPORT.value -> {
                atvTitle.text = getString(R.string.report_intake_form)
                btnSubmit.text = getString(R.string.submit)

                tilFirstName.hint = getString(R.string.first_name_mandatory)
                type=AstrologersListType.REPORT.value

                //etFirstName.setHint(getString(R.string.first_name_mandatory))

                tilLastName.visible()
                tilState.visible()
                tilCity.visible()
                tilCountry.visible()
                tilMaritalStatus.visible()
                maritalStatusRV.visible()
                tilTopiOfConcerns.visible()
                concernsRV.visible()
                tilOccupation.visible()

                tilPartnerLastName.visible()
                tilPartnerState.visible()
                tilPartnerCity.visible()
                tilPartnerCountry.visible()

                tilAnswerLanguage.visible()
                answerLanguageRV.visible()

                atvLabelComment.visible()
                aetComments.visible()
                atvTextCount.visible()

            }

            else -> {
                atvTitle.text = getString(R.string.free_intake_form)
                btnSubmit.text = getString(R.string.submit)
            }
        }

        val languages = intent?.getStringExtra(AppConstants.KEY_LANGUAGES) ?: ""

        val languagesArray = languages.split(",")
        for (data in languagesArray) {
            languagesList.add(DetailFormSelectionModel(data, false))
        }

        if (languagesList.size > 0) {
            languagesList[0].isSelected = true
            etAnswerLanguage.setText(languagesList[0].title)
        }

        genderList.add(DetailFormSelectionModel("Male", false))
        genderList.add(DetailFormSelectionModel("Female", false))

        maritalStatusList.add(DetailFormSelectionModel("Single", false))
        maritalStatusList.add(DetailFormSelectionModel("Married", false))
        maritalStatusList.add(DetailFormSelectionModel("Divorced", false))
        maritalStatusList.add(DetailFormSelectionModel("Separated", false))
        maritalStatusList.add(DetailFormSelectionModel("Widowed", false))

    }


    override fun setObservers() {
        viewModel?.intakeFormData?.observe(this, Observer {
            formData = it
            setFormData()
        })


        viewModel?.concernData?.observe(this, Observer {
            concernsList.clear()
            concernsList.addAll(it)
        })


        viewModel?.validationError?.observe(this, Observer {
            showShortToast(it)
        })


        viewModel?.saveIntakeFormResponse?.observe(this, Observer {
            hideProgressBar()

            it.data?.let { data ->

                when (viewModel?.type) {
                    AstrologersListType.CHAT.value -> {
                        viewModel?.initChat(astrologerId)
//                        viewModel?.sendChatRequest(astrologerId)
                    }

                    AstrologersListType.CALL.value -> {
                        viewModel?.initCallRequest(
                            astrologerId,
                            amount.toString(),
                            getWalletBalance()
                        )
                    }

                    AstrologersListType.REPORT.value -> {
                        viewModel?.initReportRequest(
                            astrologerId,
                            amount.toString(),
                            formData
                        )
                    }

                    else -> {
                        showShortToast(it.msg)
                    }
                }
            }

        })


        viewModel?.initChatLiveData?.observe(this, Observer {
            hideProgressBar()

//            viewModel?.clearIntakeFormData()

            it?.data?.let { data ->
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(AppConstants.KEY_CHAT_ID, data.chatId)
                intent.putExtra(AppConstants.KEY_NUMBER, astrologerId)
                intent.putExtra(AppConstants.KEY_TITLE, astrologerName)
                intent.putExtra(AppConstants.KEY_IMAGE, astrologerImageUrl)
                intent.putExtra(AppConstants.KEY_MESSAGE, data.message)
                intent.putExtra(AppConstants.KEY_IS_SHOWING_HISTORY, false)
                intent.putExtra(AppConstants.KEY_IS_INITIATING_CHAT, true)
                startActivity(intent)

                setResult(Activity.RESULT_OK)
                finish()
            }

            BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateChatEvent,type,viewModel?.getPhone()!!, amount,CapturedEvents.INITIATED,astrologerName)

        })


        viewModel?.chatRequestLiveData?.observe(this, Observer {
            hideProgressBar()

//            viewModel?.clearIntakeFormData()

            AppAlertDialog(
                this@DetailsFormActivity,
                R.drawable.ic_alert,
                getString(R.string.success),
                getString(R.string.chat_request_submitted_successfully),
                getString(R.string.ok),
                null,
                onPositiveBtnClicked = {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            ).show()
            BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateChatEvent,type,viewModel?.getPhone()!!, amount,CapturedEvents.SUBMITED,astrologerName)



        })


        viewModel?.callRequestLiveData?.observe(this, Observer {
            hideProgressBar()

//            viewModel?.clearIntakeFormData()

            AppAlertDialog(
                this,
                R.drawable.ic_checked,
                getString(R.string.success),
                getString(R.string.your_call_request_has_been_submitted),
                getString(R.string.ok),
                null,
                onPositiveBtnClicked = {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            ).show()
            BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateCallEvent,type,viewModel?.getPhone()!!, amount,CapturedEvents.SUBMITED,astrologerName)


        })


        viewModel?.reportRequestLiveData?.observe(this, Observer {
            hideProgressBar()

//            viewModel?.clearIntakeFormData()

            AppAlertDialog(
                this,
                R.drawable.ic_checked,
                getString(R.string.success),
                getString(R.string.your_report_request_has_been_submitted),
                getString(R.string.ok),
                null,
                onPositiveBtnClicked = {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            ).show()
            BaseApplication.instance.captureCallChatReportEvent(CustomEvents.InitiateReportEvent,type,viewModel?.getPhone()!!, amount,CapturedEvents.SUBMITED,astrologerName)


        })


        aivBack.setOnClickListener(this)
        atvAddPartnerDetails.setOnClickListener(this)
        etGender.setOnClickListener(this)
        etMaritalStatus.setOnClickListener(this)
        etDOB.setOnClickListener(this)
        etTimeOfBirth.setOnClickListener(this)
        etAnswerLanguage.setOnClickListener(this)
        etTopicOfConcern.setOnClickListener(this)
        etPartnerDOB.setOnClickListener(this)
        etPartnerTimeOfBirth.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

        aetComments.doAfterTextChanged {
            atvTextCount.text = ("${aetComments.text.toString().length}/400")
        }

    }




    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvAddPartnerDetails -> {
                if (atvAddPartnerDetails.isSelected) {
                    atvAddPartnerDetails.isSelected = false
                    atvAddPartnerDetails.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_unselected_radio_button, 0, 0, 0
                    )
                    llPartnerDetails.gone()
                } else {
                    atvAddPartnerDetails.isSelected = true
                    atvAddPartnerDetails.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_checked_primary_circular, 0, 0, 0
                    )
                    llPartnerDetails.visible()
                }
            }

            R.id.etDOB -> {
                selectDateOfBirth(etDOB, etDOB.text.toString())
            }

            R.id.etTimeOfBirth -> {
                selectTimeOfBirth(etTimeOfBirth)
            }

            R.id.etGender -> {
                selectGender()
            }

            R.id.etAnswerLanguage -> {
                selectLanguage()
            }

            R.id.etMaritalStatus -> {
                selectMaritalStatus()
            }

            R.id.etTopicOfConcern -> {
                selectConcern()
            }

            R.id.etPartnerDOB -> {
                selectDateOfBirth(etPartnerDOB, etPartnerDOB.text.toString())
            }

            R.id.etPartnerTimeOfBirth -> {
                selectTimeOfBirth(etPartnerTimeOfBirth)
            }

            R.id.btnSubmit -> {
                submitActionClicked()
            }
        }
    }


    private fun setFormData() {
        etFirstName?.setText(formData.firstName)
        etLastName?.setText(formData.lastName)
        aetNumber?.setText(formData.mobileNumber)
        etDOB?.setText(formData.dateOfBirth)
        etTimeOfBirth?.setText(formData.timeOfBirth)
        etPlaceOfBirth?.setText(formData.placeOfBirth)

        if (formData.gender.isNullOrBlank()) {
            etGender?.setText(genderList[0].title)
        } else{
            etGender?.setText(formData.gender)
        }
        genderList.forEach {
            it.isSelected = it.title.equals(etGender?.text.toString(), true)
        }

        if (viewModel?.type.equals(AstrologersListType.REPORT.value, true)) {

            etState?.setText(formData.state)
            etCity?.setText(formData.city)
            etCountry?.setText(formData.country)
            etOccupation?.setText(formData.occupation)
            aetComments?.setText(formData.comment)

            etTopicOfConcern?.setText(formData.topicOfConcern)
            concernsList.forEach {
                it.isSelected = it.title.equals(formData.topicOfConcern, true)
            }

            etMaritalStatus?.setText(formData.maritalStatus)
            maritalStatusList.forEach {
                it.isSelected = it.title.equals(formData.maritalStatus, true)
            }

            if (formData.isPartnerSelected) {
                atvAddPartnerDetails.isSelected = true
                etPartnerFirstName.setText(formData.partnerFirstName)
                etPartnerLastName.setText(formData.partnerlastName)
                etPartnerDOB.setText(formData.partnerDateOfBirth)
                etPartnerTimeOfBirth.setText(formData.partnerTimeOfBirth)
                etPartnerPlaceOfBirth.setText(formData.partnerPlaceOfBirth)
                etPartnerState.setText(formData.partnerState)
                etPartnerCity.setText(formData.partnerCity)
                etPartnerCountry.setText(formData.partnerCountry)
            }

        }

        setPartnerDetails()

    }


    private fun setPartnerDetails() {
        if (etMaritalStatus.text.toString().isBlank() || etMaritalStatus.text.toString()
                .equals("Single", true)
        ) {
            atvAddPartnerDetails.gone()
            llPartnerDetails.gone()

        } else {
            atvAddPartnerDetails.visible()

            if (atvAddPartnerDetails.isSelected) {
                atvAddPartnerDetails.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked_primary_circular, 0, 0, 0
                )
                llPartnerDetails.visible()

            } else {
                atvAddPartnerDetails.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_unselected_radio_button, 0, 0, 0
                )

                llPartnerDetails.gone()
            }
        }
    }


    private fun selectDateOfBirth(edittext: EditText, dateOfBirth: String) {
        val calender = Calendar.getInstance()

        if (!dateOfBirth.isBlank()) {
            try {
                val dateFormat = SimpleDateFormat(AppUtils.DF_dd_MMMM_yyyy, Locale.getDefault())
                val date = dateFormat.parse(dateOfBirth)
                if (date != null)
                    calender.timeInMillis = date.time

            } catch (e: Exception) {

            }
        }


//        val datePickerDialog = DatePickerDialog.newInstance(
//            object : DatePickerDialog.OnDateSetListener {
//                override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
//                    edittext.setText(
//                        AppUtils.parseDateTimeFormat(
//                            "$dayOfMonth ${monthOfYear + 1} $year",
//                            AppUtils.DF_dd_MM_yyyy,
//                            AppUtils.DF_dd_MMMM_yyyy
//                        )
//                    )
//                }
//
//            },
//            calender.get(Calendar.YEAR),
//            calender.get(Calendar.MONTH),
//            calender.get(Calendar.DAY_OF_MONTH)
//        )
//        datePickerDialog.isThemeDark = false
//        datePickerDialog.dismissOnPause(true)
//
//        val maxDate = Calendar.getInstance()
//        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.get(Calendar.DAY_OF_MONTH) - 1)
//        datePickerDialog.maxDate = maxDate
//
//        datePickerDialog.show(this.fragmentManager, "")



        //new code
        val datePickerDialog = android.app.DatePickerDialog(
            this,R.style.MyDatePickerStyle,
            android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                edittext.setText(
                        AppUtils.parseDateTimeFormat(
                            "$dayOfMonth ${monthOfYear + 1} $year",
                            AppUtils.DF_dd_MM_yyyy,
                            AppUtils.DF_dd_MMMM_yyyy
                        )
                    )

            }, calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )


        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.get(Calendar.DAY_OF_MONTH) - 1)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis


        datePickerDialog.show()
    }


    private fun selectTimeOfBirth(edittext: EditText) {
        val timePickerDialog =
            TimePickerDialog.newInstance(object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(
                    view: RadialPickerLayout?,
                    hourOfDay: Int,
                    minute: Int,
                    second: Int
                ) {
                    edittext.setText(
                        AppUtils.parseDateTimeFormat(
                            "$hourOfDay:$minute",
                            AppUtils.DF_HH_mm,
                            AppUtils.DF_hh_mm_aa
                        )
                    )
                }
            }, 0, 0, 0, false)

        timePickerDialog.isThemeDark = false
        timePickerDialog.dismissOnPause(true)
        timePickerDialog.show(this.fragmentManager, "")
    }


    private fun selectGender() {
        if (genderAdapter == null) {
            genderAdapter =
                DetailFormSelectionAdapter(this, genderList, object : DetailFormSelection {
                    override fun onItemSelection(position: Int) {
                        etGender.setText(genderList[position].title)

                        genderList.forEach {
                            it.isSelected = false
                        }

                        genderList[position].isSelected = true

                        genderSelectionRV.adapter = null
                    }

                })
        }
        genderSelectionRV.adapter = genderAdapter

    }


    private fun selectMaritalStatus() {
        if (maritalStatusAdapter == null) {
            maritalStatusAdapter =
                DetailFormSelectionAdapter(
                    this,
                    maritalStatusList,
                    object : DetailFormSelection {
                        override fun onItemSelection(position: Int) {
                            etMaritalStatus.setText(maritalStatusList[position].title)

                            maritalStatusList.forEach {
                                it.isSelected = false
                            }

                            maritalStatusList[position].isSelected = true

                            maritalStatusRV.adapter = null

                            setPartnerDetails()
                        }

                    })
        }
        maritalStatusRV.adapter = maritalStatusAdapter
    }


    private fun selectLanguage() {
        if (languageAdapter == null) {
            languageAdapter =
                DetailFormSelectionAdapter(this, languagesList, object : DetailFormSelection {
                    override fun onItemSelection(position: Int) {
                        etAnswerLanguage.setText(languagesList[position].title)

                        languagesList.forEach {
                            it.isSelected = false
                        }

                        languagesList[position].isSelected = true

                        answerLanguageRV.adapter = null
                    }

                })
        }
        answerLanguageRV.adapter = languageAdapter

    }


    private fun selectConcern() {
        if (concernsAdapter == null) {
            concernsAdapter =
                DetailFormSelectionAdapter(this, concernsList, object : DetailFormSelection {
                    override fun onItemSelection(position: Int) {
                        etTopicOfConcern.setText(concernsList[position].title)

                        concernsList.forEach {
                            it.isSelected = false
                        }

                        concernsList[position].isSelected = true

                        concernsRV.adapter = null
                    }

                })
        }
        concernsRV.adapter = concernsAdapter
    }


    private fun submitActionClicked() {
        formData.firstName = etFirstName.text.toString().trim()
        formData.lastName = etLastName.text.toString().trim()
        formData.mobileNumber = aetNumber.text.toString().trim()
        formData.gender = etGender.text.toString().trim()
        formData.dateOfBirth = etDOB.text.toString().trim()
        formData.timeOfBirth = etTimeOfBirth.text.toString().trim()
        formData.placeOfBirth = etPlaceOfBirth.text.toString().trim()
        formData.state = etState.text.toString().trim()
        formData.city = etCity.text.toString().trim()
        formData.country = etCountry.text.toString().trim()
        formData.maritalStatus = etMaritalStatus.text.toString().trim()
        formData.occupation = etOccupation.text.toString().trim()
        formData.topicOfConcern = etTopicOfConcern.text.toString().trim()
        formData.comment = aetComments.text.toString().trim()

        formData.isPartnerSelected = atvAddPartnerDetails.isSelected &&
                formData.maritalStatus.toString().isNotBlank() &&
                !formData.maritalStatus.toString().equals("Single", true)

        if (formData.isPartnerSelected) {
            formData.partnerFirstName = etPartnerFirstName.text.toString().trim()
            formData.partnerlastName = etPartnerLastName.text.toString().trim()
            formData.partnerDateOfBirth = etPartnerDOB.text.toString().trim()
            formData.partnerTimeOfBirth = etPartnerTimeOfBirth.text.toString().trim()
            formData.partnerPlaceOfBirth = etPartnerPlaceOfBirth.text.toString().trim()
            formData.partnerState = etPartnerState.text.toString().trim()
            formData.partnerCity = etPartnerCity.text.toString().trim()
            formData.partnerCountry = etPartnerCountry.text.toString().trim()

        } else {
            formData.partnerFirstName = ""
            formData.partnerlastName = ""
            formData.partnerDateOfBirth = ""
            formData.partnerTimeOfBirth = ""
            formData.partnerPlaceOfBirth = ""
            formData.partnerState = ""
            formData.partnerCity = ""
            formData.partnerCountry = ""
        }

        if (viewModel?.validateForm(formData, resources) == true) {

            FormSubmissionConfirmationDialog(
                this,
                formData.dateOfBirth,
                formData.timeOfBirth,
                formData.placeOfBirth,
                onPositiveBtnClicked = {

                    viewModel?.saveFormOnServerAndDatabase(
                        formData,
                        etAnswerLanguage.text.toString().trim()
                    )

                }
            ).show()
        }

    }


    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

}