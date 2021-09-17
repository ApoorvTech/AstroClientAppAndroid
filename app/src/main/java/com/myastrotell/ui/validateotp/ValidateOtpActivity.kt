package com.myastrotell.ui.validateotp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityValidateOtpBinding
import com.myastrotell.ui.enterpassword.EnterPasswordActivity
import com.myastrotell.ui.login.LoginSignUpActivity
import com.myastrotell.ui.login.LoginSignUpViewModel
import com.myastrotell.ui.resetpassword.ResetPasswordActivity
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_validate_otp.*


class ValidateOtpActivity : BaseActivity<ActivityValidateOtpBinding, LoginSignUpViewModel>(),
    View.OnClickListener, View.OnKeyListener {

    private var isSigningUp = false

    private var mCountDownTimer: CountDownTimer? = null

    private var number: String = ""
    private var hasInput = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        aetOtp1.requestFocus()

        startTimer()

    }


    override fun getLayoutId() = R.layout.activity_validate_otp

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<LoginSignUpViewModel>()


    private fun getData() {
        isSigningUp =
            intent?.getStringExtra(AppConstants.KEY_FROM)
                .equals(LoginSignUpActivity::class.java.name, true)

        number = intent?.getStringExtra(AppConstants.KEY_NUMBER) ?: ""

        atvLabelVerifyNumber.text = ("${getString(R.string.verify_mobile_number)} $number")

    }


    override fun initVariables() {

    }


    override fun setObservers() {
        viewModel?.validateOtpLiveData?.observe(this, Observer {
            hideProgressBar()

            if (isSigningUp) {
                // user is signing up, open enter password screen
                val intent = Intent(this, EnterPasswordActivity::class.java)
                intent.putExtra(AppConstants.KEY_FROM, ValidateOtpActivity::class.java.name)
                intent.putExtra(AppConstants.KEY_NUMBER, number)

                startActivity(intent)
                finish()

            } else {
                // user is resetting password, open reset password screen
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra(AppConstants.KEY_NUMBER, number)
                startActivity(intent)
                finish()
            }

        })


        viewModel?.sendOtpLiveData?.observe(this, Observer {
            hideProgressBar()
            startTimer()
        })


        aivBack.setOnClickListener(this)
        atvResendOtp.setOnClickListener(this)
        btnConfirm.setOnClickListener(this)


        aetOtp1.doAfterTextChanged {
            if (it.toString().isNotBlank()) {
                aetOtp1.clearFocus()
                aetOtp2.requestFocus()
            }
            enableDisableButton()
        }

        aetOtp2.doAfterTextChanged {
            if (it.toString().isNotBlank()) {
                aetOtp2.clearFocus()
                aetOtp3.requestFocus()
            }
            enableDisableButton()
        }

        aetOtp3.doAfterTextChanged {
            if (it.toString().isNotBlank()) {
                aetOtp3.clearFocus()
                aetOtp4.requestFocus()
            }
            enableDisableButton()
        }

        aetOtp4.doAfterTextChanged {
            if (it.toString().isNotBlank()) {
                aetOtp4.clearFocus()
                aetOtp5.requestFocus()
            }
            enableDisableButton()
        }

        aetOtp5.doAfterTextChanged {
            if (it.toString().isNotBlank()) {
                aetOtp5.clearFocus()
                aetOtp6.requestFocus()
            }
            enableDisableButton()
        }

        aetOtp6.doAfterTextChanged {
            if (it.toString().isNotBlank()) {
                hideSoftKeyboard()
                aetOtp6.clearFocus()
                hasInput = true
            }
            enableDisableButton()
        }

        aetOtp2.setOnKeyListener(this)
        aetOtp3.setOnKeyListener(this)
        aetOtp4.setOnKeyListener(this)
        aetOtp5.setOnKeyListener(this)
        aetOtp6.setOnKeyListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvResendOtp -> {
                if (isSigningUp) {
                    viewModel?.sendOtp(number)

                } else {
                    viewModel?.sendOtpForResetPassword(number)
                }
            }

            R.id.btnConfirm -> {
                val otp = getOtp()

                if (isValidated(otp)) {

                    if (isSigningUp) {
                        viewModel?.validateOtp(otp)

                    } else {
                        viewModel?.validateOtpForResetPassword(otp)
                    }

                }

            }
        }
    }


    private fun enableDisableButton() {
        btnConfirm.isEnabled = getOtp().isNotBlank()
    }


    /**
     * @return Entered OTP
     */
    private fun getOtp(): String {
        return StringBuilder(aetOtp1.text.toString())
            .append(aetOtp2.text.toString())
            .append(aetOtp3.text.toString())
            .append(aetOtp4.text.toString())
            .append(aetOtp5.text.toString())
            .append(aetOtp6.text.toString()).toString()

    }


    /**
     * method to validate input
     */
    private fun isValidated(otp: String): Boolean {
        if (otp.isBlank()) {
            showShortToast("Please enter otp")
            return false
        } else if (otp.length < 6) {
            showShortToast("Please enter valid otp")
            return false
        }
        return true
    }


    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL) {
            when (v?.id) {
                R.id.aetOtp2 -> {
                    if (aetOtp2.text.toString().trim().isEmpty()) {
                        aetOtp1.setText("")
                        aetOtp1.requestFocus()
                        aetOtp2.clearFocus()
                    }
                }
                R.id.aetOtp3 -> {
                    if (aetOtp3.text.toString().trim().isEmpty()) {
                        aetOtp2.setText("")
                        aetOtp2.requestFocus()
                        aetOtp3.clearFocus()
                    }
                }
                R.id.aetOtp4 -> {
                    if (aetOtp4.text.toString().trim().isEmpty()) {
                        aetOtp3.setText("")
                        aetOtp3.requestFocus()
                        aetOtp4.clearFocus()
                    }
                }
                R.id.aetOtp5 -> {
                    if (aetOtp5.text.toString().trim().isEmpty()) {
                        aetOtp4.setText("")
                        aetOtp4.requestFocus()
                        aetOtp5.clearFocus()
                    }
                }
                R.id.aetOtp6 -> {
                    if (aetOtp6.text.toString().trim().isEmpty() && !hasInput) {
                        aetOtp5.setText("")
                        aetOtp5.requestFocus()
                        aetOtp6.clearFocus()
                    } else{
                        hasInput = false
                    }
                }
            }
        }
        return false
    }


    /**
     * method to start countdown timer
     */
    private fun startTimer() {
        if (mCountDownTimer == null) {
            mCountDownTimer = object : CountDownTimer(30000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000
                    atvResendOtp.text =
                        ("${getString(R.string.resend_otp)} in ${(seconds + 1)} seconds")
                }

                override fun onFinish() {
                    stopTimer()
                }
            }
        }

        atvResendOtp.isEnabled = false
        mCountDownTimer?.start()
    }


    /**
     * method to stop countdown timer
     */
    private fun stopTimer() {
        if (mCountDownTimer != null) {
            atvResendOtp.text = getString(R.string.resend_otp)
            atvResendOtp.isEnabled = true
        }
    }


    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }


}