package com.myastrotell.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.databinding.ActivityLoginSignUpBinding
import com.myastrotell.ui.enterpassword.EnterPasswordActivity
import com.myastrotell.ui.home.HomeActivity
import com.myastrotell.ui.validateotp.ValidateOtpActivity
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.invisible
import kotlinx.android.synthetic.main.activity_login_sign_up.*


class LoginSignUpActivity : BaseActivity<ActivityLoginSignUpBinding, LoginSignUpViewModel>(),
    View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setListeners()

        DataManager.saveStringInPreference(PreferenceManager.DEVICE_ID, AppUtils.getDeviceId(this))

        if (isTaskRoot) {
            // hide back button if its the root of task-stack
            aivBack.invisible()
        }

    }

    override fun getLayoutId() = R.layout.activity_login_sign_up


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<LoginSignUpViewModel>()


    override fun initVariables() {
    }


    override fun setObservers() {
        viewModel?.checkStatusLiveData?.observe(this, Observer {
            hideProgressBar()

            // if existing user, open enter password screen
            val intent = Intent(this, EnterPasswordActivity::class.java)
            intent.putExtra(AppConstants.KEY_NUMBER, aetNumber.text.toString().trim())
            intent.putExtra(AppConstants.KEY_FROM, LoginSignUpActivity::class.java.name)
            startActivity(intent)

        })


        viewModel?.sendOtpLiveData?.observe(this, Observer {
            hideProgressBar()

            val intent = Intent(this, ValidateOtpActivity::class.java)
            intent.putExtra(AppConstants.KEY_NUMBER, aetNumber.text.toString().trim())
            intent.putExtra(AppConstants.KEY_FROM, LoginSignUpActivity::class.java.name)
            startActivity(intent)

        })

    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        // if new user, hit send otp api to initiate signUp flow
        if (responseModel?.apiRequestCode == ApiRequestCodes.CHECK_PROFILE_STATUS &&
            responseModel.code == ApiStatusCodes.NO_DATA_FOUND
        ) {

            viewModel?.sendOtp(aetNumber.text.toString())

        } else {
            super.handleApiErrorResponse(responseModel)
        }
    }


    private fun setListeners() {

        aetNumber.doAfterTextChanged { str ->
            if (str.toString().isBlank()) {
                rlEnterNumber.isSelected = false
                aivCross.visibility = View.INVISIBLE
                atvLabelMobileNumber.visibility = View.INVISIBLE
                atvCountryCode.setTextColor(
                    ContextCompat.getColor(this@LoginSignUpActivity, R.color.colorHint)
                )
            } else {
                rlEnterNumber.isSelected = true
                aivCross.visibility = View.VISIBLE
                atvLabelMobileNumber.visibility = View.VISIBLE
                atvCountryCode.setTextColor(
                    ContextCompat.getColor(this@LoginSignUpActivity, R.color.colorBlack)
                )
            }

            btnProceed.isEnabled = str.toString().length == 10
        }

        aivBack.setOnClickListener(this)
        atvSkip.setOnClickListener(this)
        btnProceed.setOnClickListener(this)
        aivCross.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvSkip -> {
                // updaing details guest login
                viewModel?.setGuestDetails()

                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            R.id.aivCross -> {
                aetNumber.setText("")
            }

            R.id.btnProceed -> {
                viewModel?.checkProfileStatus(aetNumber.text.toString())
            }
        }
    }


}