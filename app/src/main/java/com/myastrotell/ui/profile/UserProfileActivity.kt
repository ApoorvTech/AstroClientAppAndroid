package com.myastrotell.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.databinding.ActivityUserProfileBinding
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class UserProfileActivity : BaseActivity<ActivityUserProfileBinding, UserProfileViewModel>(),
    View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.profile)
        aivEnd.setImageResource(R.drawable.ic_edit_white)
        llOptions.visible()

        viewModel?.getSavedProfileDetails()

    }


    override fun getLayoutId() = R.layout.activity_user_profile


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<UserProfileViewModel>()


    override fun initVariables() {

    }


    override fun setObservers() {
        viewModel?.mProfileLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.profileDataList?.let { profileData ->
                viewModel?.saveProfileDetailsInDB(profileData)
            }

        })


        viewModel?.savedProfileLiveData?.observe(this, Observer {
            hideProgressBar()

            if (it != null) {
                setProfileData(it)

            } else {
                viewModel?.getProfileDetails()
            }

        })

        aivBack.setOnClickListener(this)
        aivEnd.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.aivEnd -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivityForResult(intent, AppConstants.EDIT_PROFILE_REQUEST_CODE)
            }
        }
    }


    private fun setProfileData(data: UserProfileEntity) {
        llMain.visible()

        if (!data.profilePicUrl.isNullOrBlank())
            sdvImage.setImageURI(data.profilePicUrl)


        if (data.firstName.isNullOrBlank())
            atvFirstName.text = ("NA")
        else
            atvFirstName.text = data.firstName


        if (data.lastName.isNullOrBlank())
            atvLastName.text = ("NA")
        else
            atvLastName.text = data.lastName


        if (data.msisdn.isBlank())
            atvPhoneNumber.text = ("NA")
        else
            atvPhoneNumber.text = data.msisdn


        if (data.email.isNullOrBlank())
            atvEmail.text = ("NA")
        else
            atvEmail.text = data.email


        if (data.gender.isNullOrBlank())
            atvGender.text = ("NA")
        else
            atvGender.text = data.gender


        if (data.dateOfBirth.isNullOrBlank())
            atvDob.text = ("NA")
        else
            atvDob.text = data.dateOfBirth


        if (data.timeOfBirth.isNullOrBlank())
            atvTob.text = ("NA")
        else
            atvTob.text = data.timeOfBirth


        if (data.placeOfBirth.isNullOrBlank())
            atvPob.text = ("NA")
        else
            atvPob.text = data.placeOfBirth


        if (data.city.isNullOrBlank())
            atvCity.text = ("NA")
        else
            atvCity.text = data.city


        if (data.state.isNullOrBlank())
            atvState.text = ("NA")
        else
            atvState.text = data.state


        if (data.country.isNullOrBlank())
            atvCountry.text = ("NA")
        else
            atvCountry.text = data.country


        if (data.maritalStatus.isNullOrBlank())
            atvMaritalStatus.text = ("NA")
        else
            atvMaritalStatus.text = data.maritalStatus


        if (data.occupation.isNullOrBlank())
            atvOccupation.text = ("NA")
        else
            atvOccupation.text = data.occupation


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.EDIT_PROFILE_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    viewModel?.getSavedProfileDetails()
                }
            }
        }
    }


}