package com.myastrotell.ui.callreview

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityCallReviewBinding
import com.myastrotell.pojo.response.ProductBillingDetails
import com.myastrotell.ui.orderhistory.OrderHistoryViewModel
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_call_review.*
import kotlinx.android.synthetic.main.layout_submit_reviews.*


class CallReviewActivity : BaseActivity<ActivityCallReviewBinding, OrderHistoryViewModel>(),
    View.OnClickListener {

    private var orderData: ProductBillingDetails? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        orderData?.let {
            viewModel?.getOrderDetails(it.id)
        }

//        aetReview.filters = AppUtils.getOmitEmojiFilter()

    }


    private fun getData() {
        orderData = intent?.getParcelableExtra(AppConstants.KEY_DATA)

        orderData?.let {

            atvTitle.text = it.productName

            sdvImage.setImageURI(it.imageURL)


        }

    }


    override fun getLayoutId() = R.layout.activity_call_review


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<OrderHistoryViewModel>()


    override fun initVariables() {

    }


    override fun setObservers() {
        viewModel?.orderDetailsLiveData?.observe(this, Observer {
            hideProgressBar()

            clMain.visible()

        })


        aivBack.setOnClickListener(this)
        atvSubmit.setOnClickListener(this)

        aetReview.doAfterTextChanged {
            atvReviewTextCount.text = ("${it.toString().length}/160")
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvSubmit -> {
                if (reviewRatingBar.rating > 0) {

                } else {
                    showShortToast("Please provide rating")
                }
            }
        }
    }


}