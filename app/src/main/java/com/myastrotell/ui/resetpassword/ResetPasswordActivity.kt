package com.myastrotell.ui.resetpassword

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityResetPasswordBinding
import com.myastrotell.ui.login.LoginSignUpViewModel
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_reset_password.*


class ResetPasswordActivity : BaseActivity<ActivityResetPasswordBinding, LoginSignUpViewModel>(),
    View.OnClickListener {

    private lateinit var number: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        aetNewPassword.requestFocus()

    }


    override fun getLayoutId() = R.layout.activity_reset_password


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<LoginSignUpViewModel>()


    override fun initVariables() {

    }


    override fun setObservers() {
        viewModel?.resetPasswordLiveData?.observe(this, Observer {
            hideProgressBar()

            showShortToast(it?.msg)

            finish()

        })


        aivBack.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)


        aetNewPassword.doAfterTextChanged {
            enableDisableSubmitButton()
        }

        aetConfirmPassword.doAfterTextChanged {
            enableDisableSubmitButton()
        }

    }


    private fun getData() {
        number = intent?.getStringExtra(AppConstants.KEY_NUMBER) ?: ""

        atvLabelMobileNumber.text = ("${getString(R.string.for_mobile_number)} $number")

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.btnSubmit -> {
                if (isValidated()) {
                    viewModel?.resetPassword(aetNewPassword.text.toString().trim())
                }
            }
        }
    }


    private fun enableDisableSubmitButton() {
        btnSubmit.isEnabled = (aetNewPassword.text.toString().isNotBlank()
                && aetConfirmPassword.text.toString().isNotBlank())

    }


    private fun isValidated(): Boolean {
        if (aetNewPassword.text.toString().isBlank()) {
            showShortToast("Please enter new password")
            return false

        } else if (aetNewPassword.text.toString().length < 8) {
            showShortToast("Password should have at least 8 characters")
            return false

        } else if (aetConfirmPassword.text.toString().isBlank()) {
            showShortToast("Please confirm new password")
            return false

        } else if (!aetNewPassword.text.toString().equals(aetConfirmPassword.text.toString())) {
            showShortToast("Password does not match")
            return false
        }

        return true
    }

}