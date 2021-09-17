package com.myastrotell.ui.enterpassword

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.myastrotell.BaseApplication
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityEnterPasswordBinding
import com.myastrotell.ui.home.HomeActivity
import com.myastrotell.ui.login.LoginSignUpActivity
import com.myastrotell.ui.login.LoginSignUpViewModel
import com.myastrotell.ui.validateotp.ValidateOtpActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.invisible
import kotlinx.android.synthetic.main.activity_enter_password.*


class EnterPasswordActivity : BaseActivity<ActivityEnterPasswordBinding, LoginSignUpViewModel>(),
    View.OnClickListener {

    private var isLoggingIn: Boolean = false
    private var number: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()
        aetPassword.requestFocus()

    }


    override fun getLayoutId() = R.layout.activity_enter_password


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<LoginSignUpViewModel>()


    private fun getData() {
        isLoggingIn =
            intent?.getStringExtra(AppConstants.KEY_FROM)
                .equals(LoginSignUpActivity::class.java.name, true)

        number = intent?.getStringExtra(AppConstants.KEY_NUMBER) ?: ""

        if (!isLoggingIn) {
            atvForgotPassword.invisible()
            atvLabelWelcomeBack.text = getString(R.string.welcome)
        }

    }


    override fun initVariables() {

    }


    override fun setObservers() {
        viewModel?.loginLiveData?.observe(this, Observer {
            hideProgressBar()

            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        })

        viewModel?.eventTrackeLiveData?.observe(this, Observer {
            it?.let {
                BaseApplication.instance.captureLoginEvent(it,number)
            }
        })


        viewModel?.createUserProfileLiveData?.observe(this, Observer {
            hideProgressBar()

            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            BaseApplication.instance.captureSignupEvent(number)




        })


        viewModel?.sendOtpLiveData?.observe(this, Observer {
            hideProgressBar()

            val intent = Intent(this, ValidateOtpActivity::class.java)
            intent.putExtra(AppConstants.KEY_NUMBER, number)
            intent.putExtra(AppConstants.KEY_FROM, EnterPasswordActivity::class.java.name)
            startActivity(intent)

        })


        aetPassword.doAfterTextChanged {
            btnSubmit.isEnabled = it.toString().isNotBlank()
        }


        aivBack.setOnClickListener(this)
        atvForgotPassword.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvForgotPassword -> {
                viewModel?.sendOtpForResetPassword(number)
            }

            R.id.btnSubmit -> {
                val password = aetPassword.text.toString()

                if (isLoggingIn) {
                    viewModel?.login(password)

                } else {

                    if (isValidated(password)) {
                        viewModel?.createUserProfile(password)
                    }

                }
            }
        }
    }


    private fun isValidated(password: String): Boolean {
        if (password.isBlank()) {
            showShortToast("Please enter password")
            return false

        } else if (password.length < 8) {
            showShortToast("Password should have at least 8 characters")
            return false

        }
        return true
    }

}