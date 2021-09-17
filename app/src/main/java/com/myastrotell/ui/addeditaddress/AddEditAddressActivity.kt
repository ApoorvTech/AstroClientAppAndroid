package com.myastrotell.ui.addeditaddress

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityAddEditAddressBinding
import com.myastrotell.pojo.requests.AddEditAddressRequest
import com.myastrotell.pojo.requests.AddressRequest
import com.myastrotell.pojo.response.AddressListResponse
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class AddEditAddressActivity :
    BaseActivity<ActivityAddEditAddressBinding, AddEditAddressViewModel>(),
    View.OnClickListener {

    private var isEditing = false
    private var mData: AddressListResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle?.text = getString(R.string.add_new_address)
        aivEnd.setImageResource(R.drawable.ic_cross_white)
        aivBack.gone()
        llOptions.visible()

        getData()

        aetNumber?.setText(viewModel?.getMsisdn())

    }


    override fun getLayoutId() = R.layout.activity_add_edit_address


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<AddEditAddressViewModel>()


    override fun initVariables() {
    }


    private fun getData() {
        isEditing = intent?.hasExtra(AppConstants.KEY_DATA) ?: false

        mData = intent.getParcelableExtra(AppConstants.KEY_DATA)

        mData?.let {
            aetName.setText(it.userName ?: "")
            aetAlternateNumber.setText(it.mobile ?: "")
            aetFlatNumber.setText(it.houseNo ?: "")
            aetLocality.setText(it.addressLine1 ?: "")
            aetLandmark.setText(it.addressLine2 ?: "")
            aetState.setText(it.state ?: "")
            aetCity.setText(it.city ?: "")
            aetCountry.setText(it.country ?: "")
            aetPinCode.setText(it.pinCode ?: "")
        }

    }


    override fun setObservers() {
        viewModel?.addEditAddressLiveData?.observe(this, Observer {
            hideProgressBar()

            showShortToast(it?.msg)

            setResult(Activity.RESULT_OK)
            finish()

        })


        aivEnd.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivEnd -> {
                onBackPressed()
            }

            R.id.btnSubmit -> {
                if (isValidated()) {

                    val address = AddressRequest(
                        userId = viewModel?.getMsisdn(),
                        userName = aetName.text?.trim().toString(),
                        mobile = aetAlternateNumber.text?.trim().toString(),
                        houseNo = aetFlatNumber.text?.trim().toString(),
                        addressLine1 = aetLocality.text?.trim().toString(),
                        addressLine2 = aetLandmark.text?.trim().toString(),
                        state = aetState.text?.trim().toString(),
                        city = aetCity.text?.trim().toString(),
                        country = aetCountry.text?.trim().toString(),
                        pinCode = aetPinCode.text?.trim().toString()
                    )

                    val addressList = ArrayList<AddressRequest>()
                    addressList.add(address)

                    val request = AddEditAddressRequest(addressList)


                    if (isEditing) {
                        request.userAddressList[0].addressId = mData?.addressId ?: ""
                        viewModel?.editAddress(request)

                    } else {
                        viewModel?.saveAddress(request)
                    }

                }
            }
        }
    }


    private fun isValidated(): Boolean {
        return when {
            aetName.text?.trim().toString().isBlank() -> {
                showShortToast("Please enter name")
                nsvMain.post {
                    nsvMain.scrollTo(0, 0)
                }
                false

            }

            aetFlatNumber.text?.trim().toString().isBlank() -> {
                showShortToast("Please enter flat number")
                nsvMain.post {
                    nsvMain.scrollTo(0, 0)
                }
                false
            }

            aetLocality.text?.trim().toString().isBlank() -> {
                showShortToast("Please enter locality")
                nsvMain.post {
                    nsvMain.scrollTo(0, 0)
                }
                false
            }

            aetState.text?.trim().toString().isBlank() -> {
                showShortToast("Please enter state")
                false
            }

            aetCity.text?.trim().toString().isBlank() -> {
                showShortToast("Please enter city")
                false
            }

            aetCountry.text?.trim().toString().isBlank() -> {
                showShortToast("Please enter country")
                false
            }

            aetPinCode.text?.trim().toString().isBlank() -> {
                showShortToast("Please enter pin code")
                false
            }

            aetPinCode.text?.trim().toString().length < 6 -> {
                showShortToast("Please enter valid pin code")
                false
            }

            else -> {
                true
            }
        }
    }


}