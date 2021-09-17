package com.myastrotell.ui.payments.status

import android.os.Bundle
import com.myastrotell.BR
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.PaymentStatusActivityBinding
import kotlinx.android.synthetic.main.payment_status_activity.*

class StatusActivity :BaseActivity<PaymentStatusActivityBinding,BaseViewModel>(){
    override fun getLayoutId()= R.layout.payment_status_activity

    override fun getBindingVariable()=BR.viewModel

    override fun initViewModel()=null

    override fun initVariables() {
    }

    override fun setObservers() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvStatus.text=intent.getStringExtra(AppConstants.KEY_DATA)
    }

}