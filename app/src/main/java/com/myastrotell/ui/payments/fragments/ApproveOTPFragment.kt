package com.myastrotell.ui.payments.fragments

import android.os.Bundle
import android.view.View
import com.myastrotell.BR
import com.myastrotell.R
import com.myastrotell.base.BaseFragment
import com.myastrotell.base.BaseViewModel
import com.myastrotell.databinding.FragmentPaymentOtpBinding
import com.myastrotell.ui.payments.interfaces.Communicator
import kotlinx.android.synthetic.main.fragment_city_bank.*
import kotlinx.android.synthetic.main.fragment_payment_otp_approval.*

class ApproveOTPFragment : BaseFragment<FragmentPaymentOtpBinding, BaseViewModel>() {

    private lateinit var communicator: Communicator
    private var otpText:String=""

    override fun getLayoutId() = R.layout.fragment_payment_otp_approval

    override fun getBindingVariable() = BR.viewModel

    override fun initViewModel() = null

    override fun initVariables() {
    }

    override fun setObservers() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        approve.setOnClickListener{
            communicator.respond(otpText)
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator= activity as Communicator
        textView.text = otpText

    }

    fun setOtpText(data:String){
        otpText=data
    }
}
