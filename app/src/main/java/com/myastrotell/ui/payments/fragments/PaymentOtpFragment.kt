package com.myastrotell.ui.payments.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.myastrotell.BR
import com.myastrotell.R
import com.myastrotell.base.BaseFragment
import com.myastrotell.base.BaseViewModel
import com.myastrotell.databinding.FragmentPaymentOtpBinding
import kotlinx.android.synthetic.main.fragment_payment_otp.*

class PaymentOtpFragment :BaseFragment<FragmentPaymentOtpBinding,BaseViewModel>(){
    override fun getLayoutId()= R.layout.fragment_payment_otp

    override fun getBindingVariable()=BR.viewModel

    override fun initViewModel()=null

    override fun initVariables() {
    }

    override fun setObservers() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val time=object:CountDownTimer(30000,10000){
            override fun onFinish() {

            }

            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "Time remaining: " + millisUntilFinished / 1000;
            }


        }
        time.start()
    }

}