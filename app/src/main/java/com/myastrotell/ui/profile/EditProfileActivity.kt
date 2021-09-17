package com.myastrotell.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.adapters.DetailFormSelectionAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.databinding.ActivityEditProfileBinding
import com.myastrotell.interfaces.DetailFormSelection
import com.myastrotell.pojo.DetailFormSelectionModel
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.AppUtils.isEmailValid
import com.myastrotell.utils.ImageUtils
import com.myastrotell.utils.getViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_details_form.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.aetNumber
import kotlinx.android.synthetic.main.activity_edit_profile.btnSubmit
import kotlinx.android.synthetic.main.activity_edit_profile.etCity
import kotlinx.android.synthetic.main.activity_edit_profile.etCountry
import kotlinx.android.synthetic.main.activity_edit_profile.etDOB
import kotlinx.android.synthetic.main.activity_edit_profile.etFirstName
import kotlinx.android.synthetic.main.activity_edit_profile.etGender
import kotlinx.android.synthetic.main.activity_edit_profile.etLastName
import kotlinx.android.synthetic.main.activity_edit_profile.etMaritalStatus
import kotlinx.android.synthetic.main.activity_edit_profile.etOccupation
import kotlinx.android.synthetic.main.activity_edit_profile.etPlaceOfBirth
import kotlinx.android.synthetic.main.activity_edit_profile.etState
import kotlinx.android.synthetic.main.activity_edit_profile.etTimeOfBirth
import kotlinx.android.synthetic.main.activity_edit_profile.genderSelectionRV
import kotlinx.android.synthetic.main.activity_edit_profile.maritalStatusRV
import kotlinx.android.synthetic.main.layout_toolbar_primary.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditProfileActivity : BaseActivity<ActivityEditProfileBinding, UserProfileViewModel>(),
    View.OnClickListener {

    private lateinit var genderList: ArrayList<DetailFormSelectionModel>
    private var genderAdapter: DetailFormSelectionAdapter? = null

    private lateinit var maritalStatusList: ArrayList<DetailFormSelectionModel>
    private var maritalStatusAdapter: DetailFormSelectionAdapter? = null

    private var cameraUri: Uri? = null
    private var croppedImageUri: Uri? = null

    private var newImagePath: String? = null

    private var savedProfileData: UserProfileEntity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.edit_profile)
        initData()

        viewModel?.getSavedProfileDetails()

    }

    override fun getLayoutId() = R.layout.activity_edit_profile


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<UserProfileViewModel>()


    override fun initVariables() {
        genderList = ArrayList()
        maritalStatusList = ArrayList()
    }


    override fun setObservers() {
        viewModel?.savedProfileLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.let { profileData ->
                savedProfileData = profileData
                setProfileData()
            }

        })


        viewModel?.uploadProfileImageLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.imageUrl?.let { imageUrl ->
                savedProfileData?.profilePicUrl = imageUrl
                updateUserProfile()
            }

        })


        viewModel?.updateProfileLiveData?.observe(this, Observer {
            hideProgressBar()

            showShortToast(it?.msg)

            it?.data?.let { profileData ->
                viewModel?.saveProfileDetailsInDB(profileData)
            }

            setResult(RESULT_OK, Intent())
            finish()

        })


        aivBack.setOnClickListener(this)
        sdvImage.setOnClickListener(this)
        atvEditImage.setOnClickListener(this)
        etDOB.setOnClickListener(this)
        etTimeOfBirth.setOnClickListener(this)
        etGender.setOnClickListener(this)
        etMaritalStatus.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

    }


    private fun initData() {
        genderList.add(DetailFormSelectionModel("Male", false))
        genderList.add(DetailFormSelectionModel("Female", false))

        maritalStatusList.add(DetailFormSelectionModel("Single", false))
        maritalStatusList.add(DetailFormSelectionModel("Married", false))
        maritalStatusList.add(DetailFormSelectionModel("Divorced", false))
        maritalStatusList.add(DetailFormSelectionModel("Separated", false))
        maritalStatusList.add(DetailFormSelectionModel("Widowed", false))
    }


    private fun setProfileData() {
        savedProfileData?.let { data ->
            if (!data.profilePicUrl.isNullOrBlank())
                sdvImage.setImageURI(data.profilePicUrl)


            if (!data.firstName.isNullOrBlank())
                etFirstName.setText(data.firstName)


            if (!data.lastName.isNullOrBlank())
                etLastName.setText(data.lastName)


            if (data.msisdn.isNotBlank())
                aetNumber.setText(data.msisdn)

           // newly added
            if (!data.email.isNullOrBlank())
                etEmail.setText(data.email)



            if (!data.dateOfBirth.isNullOrBlank())
                etDOB.setText(data.dateOfBirth)


            if (!data.timeOfBirth.isNullOrBlank())
                etTimeOfBirth.setText(data.timeOfBirth)


            if (!data.placeOfBirth.isNullOrBlank())
                etPlaceOfBirth.setText(data.placeOfBirth)


            if (!data.city.isNullOrBlank())
                etCity.setText(data.city)


            if (!data.state.isNullOrBlank())
                etState.setText(data.state)


            if (!data.country.isNullOrBlank())
                etCountry.setText(data.country)


            if (!data.occupation.isNullOrBlank())
                etOccupation.setText(data.occupation)


            if (data.gender.isNullOrBlank()) {
                etGender.setText(genderList[0].title)
                genderList[0].isSelected = true
            } else {
                etGender.setText(data.gender)
                genderList.forEach { gender ->
                    if (data.gender.equals(gender.title, true)) {
                        gender.isSelected = true
                        return@forEach
                    }
                }
            }


            if (data.maritalStatus.isNullOrBlank()) {
                etMaritalStatus.setText("")
            } else {
                etMaritalStatus.setText(data.maritalStatus)

                maritalStatusList.forEach { maritalStatus ->
                    if (data.maritalStatus.equals(maritalStatus.title, true)) {
                        maritalStatus.isSelected = true
                        return@forEach
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.sdvImage, R.id.atvEditImage -> {
                ImageUtils.showCameraGalleryDialog(this, listener = { position ->
                    when (position) {
                        0 -> {
                            if (AppUtils.checkPermissions(
                                    this, ImageUtils.CAMERA_STORAGE_PERMISSSION_ARR
                                )
                            ) {
                                openCamera()

                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    ImageUtils.CAMERA_STORAGE_PERMISSSION_ARR,
                                    AppConstants.RC_CAMERA_PERMISSION
                                )
                            }
                        }
                        1 -> {
                            if (AppUtils.checkPermissions(
                                    this, ImageUtils.STORAGE_PERMISSSION_ARR
                                )
                            ) {
                                openGallery()

                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    ImageUtils.STORAGE_PERMISSSION_ARR,
                                    AppConstants.RC_STORAGE_PERMISSION
                                )
                            }
                        }
                    }
                })

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

            R.id.etMaritalStatus -> {
                selectMaritalStatus()
            }

            R.id.btnSubmit -> {
                if (isValidated()) {
                    if (newImagePath != null) {
                        viewModel?.uploadProfileImage(newImagePath.toString())

                    } else {
                        updateUserProfile()
                    }
                }
            }
        }
    }


    private fun updateUserProfile() {
        val entity = savedProfileData ?: UserProfileEntity()
        entity.msisdn = aetNumber.text.toString().trim()

        entity.email = etEmail.text.toString() //newly added
        entity.firstName = etFirstName.text.toString().trim()
        entity.lastName = etLastName.text.toString().trim()
        entity.gender = etGender.text.toString().trim()
        entity.dateOfBirth = etDOB.text.toString().trim()
        entity.timeOfBirth = etTimeOfBirth.text.toString().trim()
        entity.placeOfBirth = etPlaceOfBirth.text.toString().trim()
        entity.city = etCity.text.toString().trim()
        entity.state = etState.text.toString().trim()
        entity.country = etCountry.text.toString().trim()
        entity.maritalStatus = etMaritalStatus.text.toString().trim()
        entity.occupation = etOccupation.text.toString().trim()
        entity.profilePicUrl = savedProfileData?.profilePicUrl ?: ""

        viewModel?.updateUserProfile(entity)
    }


    private fun openCamera() {
        cameraUri = ImageUtils.getOutputImageUri(this)
        ImageUtils.openCameraToCaptureImage(this, cameraUri)
    }


    private fun openGallery() {
        ImageUtils.openGalleryToPickImage(this)
    }


    private fun selectDateOfBirth(edittext: EditText, dateOfBirth: String) {
        val calender = Calendar.getInstance()

        if (dateOfBirth.isNotBlank()) {
            try {
                val dateFormat = SimpleDateFormat(AppUtils.DF_dd_MMMM_yyyy, Locale.ENGLISH)
                val date = dateFormat.parse(dateOfBirth)
                if (date != null)
                    calender.timeInMillis = date.time

            } catch (e: Exception) {

            }
        }

        val datePickerDialog = DatePickerDialog.newInstance(
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: DatePickerDialog?,
                    year: Int,
                    monthOfYear: Int,
                    dayOfMonth: Int
                ) {
                    edittext.setText(
                        AppUtils.parseDateTimeFormat(
                            "$dayOfMonth ${monthOfYear + 1} $year",
                            AppUtils.DF_dd_MM_yyyy,
                            AppUtils.DF_dd_MMMM_yyyy
                        )
                    )
                }

            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.isThemeDark = false
        datePickerDialog.dismissOnPause(true)

        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.get(Calendar.DAY_OF_MONTH) - 1)
        datePickerDialog.maxDate = maxDate

        datePickerDialog.show(this.fragmentManager, "")
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

                        genderList.forEach { list ->
                            list.isSelected = false
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
                DetailFormSelectionAdapter(this, maritalStatusList, object : DetailFormSelection {
                    override fun onItemSelection(position: Int) {
                        etMaritalStatus.setText(maritalStatusList[position].title)

                        maritalStatusList.forEach { list ->
                            list.isSelected = false
                        }

                        maritalStatusList[position].isSelected = true

                        maritalStatusRV.adapter = null

                    }
                })

        }
        maritalStatusRV.adapter = maritalStatusAdapter

    }


    private fun isValidated(): Boolean {
        return if (etFirstName.text.toString().isBlank()) {
            showShortToast(getString(R.string.enterFirstName))
            false
        } else if (etGender.text.toString().isBlank()) {
            showShortToast(getString(R.string.selectGender))
            false
        } else if (etDOB.text.toString().isBlank()) {
            showShortToast(getString(R.string.selectDOB))
            false
        } else if (etTimeOfBirth.text.toString().isBlank()) {
            showShortToast(getString(R.string.selectTOB))
            false
        } else if (etPlaceOfBirth.text.toString().isBlank()) {
            showShortToast(getString(R.string.enterPlaceOfBirth))
            false
        } else if (etEmail.text.toString().trim().isNotEmpty() && !isEmailValid(etEmail.text.toString())) {
            showShortToast(getString(R.string.enterValidEmail))
            false
        }

        else {
            true
        }
    }


    /**
     * method to open image cropper
     */
    private fun openCropper(imageUri: Uri) {
        croppedImageUri = Uri.fromFile(ImageUtils.getImageFile(this))
        CropImage.activity(imageUri)
            .setOutputUri(croppedImageUri)
            .setCropShape(CropImageView.CropShape.OVAL)
            .setAspectRatio(1, 1)
            .setAllowRotation(false)
            .setAllowFlipping(false)
            .setGuidelines(CropImageView.Guidelines.OFF)
            .setCropMenuCropButtonTitle("Select Photo")
            .start(this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ImageUtils.REQUEST_CAMERA_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    openCropper(cameraUri!!)
                }
            }

            ImageUtils.REQUEST_PICK_GALLERY_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    openCropper(data.data!!)
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val cropResult = CropImage.getActivityResult(data)
                    if (cropResult != null) {
                        val imageUri = cropResult.uri
                        if (imageUri != null) {
                            newImagePath = imageUri.toString()
                            sdvImage.setImageURI(newImagePath)
                        }
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.RC_CAMERA_PERMISSION -> {
                if (AppUtils.hasPermissions(grantResults)) {
                    openCamera()

                } else {
                    AppUtils.appAlertDialog(
                        this,
                        R.drawable.ic_alert,
                        getString(R.string.txt_permissions_required),
                        getString(R.string.txt_permissions_required_to_continue),
                        getString(R.string.txt_yes),
                        getString(R.string.txt_not_now),
                        positiveClickListener = {
                            if (AppUtils.shouldAskPermissionsAgain(this, permissions)) {
                                ActivityCompat.requestPermissions(
                                    this,
                                    ImageUtils.CAMERA_STORAGE_PERMISSSION_ARR,
                                    AppConstants.RC_CAMERA_PERMISSION
                                )

                            } else {
                                AppUtils.openAppSettings(this)
                            }
                        }, negativeClickListener = null
                    )
                }

            }

            AppConstants.RC_STORAGE_PERMISSION -> {
                if (AppUtils.hasPermissions(grantResults)) {
                    openGallery()

                } else {
                    AppUtils.appAlertDialog(
                        this,
                        R.drawable.ic_alert,
                        getString(R.string.txt_permissions_required),
                        getString(R.string.txt_permissions_required_to_continue),
                        getString(R.string.txt_yes),
                        getString(R.string.txt_not_now),
                        positiveClickListener = {
                            if (AppUtils.shouldAskPermissionsAgain(this, permissions)) {
                                ActivityCompat.requestPermissions(
                                    this,
                                    ImageUtils.STORAGE_PERMISSSION_ARR,
                                    AppConstants.RC_STORAGE_PERMISSION
                                )

                            } else {
                                AppUtils.openAppSettings(this)
                            }
                        }, negativeClickListener = null
                    )
                }
            }
        }
    }

}