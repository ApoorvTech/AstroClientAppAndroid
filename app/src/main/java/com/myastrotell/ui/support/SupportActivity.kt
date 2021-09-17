package com.myastrotell.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.myastrotell.BuildConfig
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.databinding.ActivitySupportNewBinding
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_support_new.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class SupportActivity : BaseActivity<ActivitySupportNewBinding, SupportViewModel>(),
    View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setData()

    }


    override fun getLayoutId() = R.layout.activity_support_new

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<SupportViewModel>()

    override fun initVariables() {

    }


    override fun setObservers() {

        viewModel?.formSubmissionLiveData?.observe(this, Observer {
            hideProgressBar()
            showLongToast(it?.msg)

            onBackPressed()
        })


        aivBack.setOnClickListener(this)
        atvWebsiteValue.setOnClickListener(this)
        atvSubmit.setOnClickListener(this)

    }


    private fun setData() {

        atvTitle.text = getString(R.string.supportTitle)

        etName.setText(viewModel?.getName())
        aetMobile.setText(viewModel?.getMsisdn())

//        aetMobile.isEnabled = aetMobile.text.toString().isBlank()

        atvAppVersion.text = ("${getString(R.string.versionSupport)}: ${BuildConfig.VERSION_NAME}")

        atvWebsiteLabel.text = ("${getString(R.string.website)} : ")
        atvWebsiteValue.text = ("https://www.myastrotell.com")

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvWebsiteValue -> {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(atvWebsiteValue.text.toString()))
                startActivity(browserIntent)
            }

            R.id.atvSubmit -> {
                if (isValidated()) {
                    viewModel?.submitFormData(
                        etName.text.toString(),
                        aetMobile.text.toString(),
                        aetEmail.text.toString(),
                        aetConcern.text.toString(),
                    )
                }
            }
        }
    }

    private fun isValidated(): Boolean {
        return when {
            etName.text.toString().isBlank() -> {
                showShortToast(getString(R.string.please_enter_name))
                false
            }
            aetMobile.text.toString().isBlank() -> {
                showShortToast(getString(R.string.please_enter_mobile_number))
                false
            }
            aetMobile.text.toString().trim().length < 10 -> {
                showShortToast(getString(R.string.please_enter_valid_number))
                false
            }
            (aetEmail.text.toString().isNotBlank() &&
                    !AppUtils.isEmailValid(aetEmail.text.toString().trim())
                    ) -> {
                showShortToast(getString(R.string.please_enter_valid_email))
                false
            }
            aetConcern.text.toString().isBlank() -> {
                showShortToast(getString(R.string.please_enter_concern))
                false
            }
            else -> {
                true
            }
        }
    }


}