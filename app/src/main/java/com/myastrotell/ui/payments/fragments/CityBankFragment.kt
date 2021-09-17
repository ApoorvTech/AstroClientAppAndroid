package com.myastrotell.ui.payments.fragments

import android.os.Bundle
import android.view.View
import com.myastrotell.BR
import com.myastrotell.R
import com.myastrotell.base.BaseFragment
import com.myastrotell.base.BaseViewModel
import com.myastrotell.databinding.FragmentCityBankBinding
import com.myastrotell.ui.payments.interfaces.Communicator
import kotlinx.android.synthetic.main.fragment_city_bank.*

class CityBankFragment :BaseFragment<FragmentCityBankBinding,BaseViewModel>(),
    View.OnClickListener {
    private lateinit var communicator: Communicator
    override fun getLayoutId()= R.layout.fragment_city_bank

    override fun getBindingVariable()=BR.viewModel

    override fun initViewModel()=null

    override fun initVariables() {
    }

    override fun setObservers() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        password.setOnClickListener(this);
        smsOtp.setOnClickListener(this);

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator= activity as Communicator

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.password->{
                communicator.respond("password")
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

            }
            R.id.smsOtp->{
                communicator.respond("smsOtp")
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

            }
        }
    }

}