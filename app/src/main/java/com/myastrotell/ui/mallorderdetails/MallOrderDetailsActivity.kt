package com.myastrotell.ui.mallorderdetails

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.MallOrderedItemAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityMallOrderDetailsBinding
import com.myastrotell.pojo.MallOrderedItemModel
import com.myastrotell.pojo.response.ProductBillingDetails
import com.myastrotell.ui.orderhistory.OrderHistoryViewModel
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_mall_order_details.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class MallOrderDetailsActivity :
    BaseActivity<ActivityMallOrderDetailsBinding, OrderHistoryViewModel>(),
    View.OnClickListener {


    private lateinit var mItemsAdapter: MallOrderedItemAdapter
    private lateinit var mItemsList: ArrayList<MallOrderedItemModel>

    private var orderData: ProductBillingDetails? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        setupAdapter()

        orderData?.let {
            viewModel?.getOrderDetails(it.id)
        }

    }


    override fun getLayoutId() = R.layout.activity_mall_order_details


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<OrderHistoryViewModel>()


    override fun initVariables() {
        mItemsList = ArrayList()
    }


    override fun setObservers() {
        viewModel?.orderDetailsLiveData?.observe(this, Observer {
            hideProgressBar()

            rlMain.visible()

            it?.data?.let { data ->

                atvCustomerName.text = data.deliveryName

                val address = StringBuilder()
                address.append(data.houseNo)

                address.append(", ")
                address.append(data.addressLine1)

                if (!data.addressLine2.isNullOrBlank()) {
                    address.append(", ")
                    address.append(data.addressLine2)
                }

                address.append(", ")
                address.append(data.state)

                address.append(", ")
                address.append(data.city)

                address.append("\n")
                address.append(data.pinCode)

                atvCustomerAddress.text = address.toString()

            }

        })


        aivBack.setOnClickListener(this)

    }


    private fun getData() {
        orderData = intent?.getParcelableExtra(AppConstants.KEY_DATA)

        orderData?.let {

            atvTitle.text = ("${getString(R.string.order_id)} ${it.id}")

            atvOrderId.text = it.id

            atvOrderDate.text = AppUtils.timeStampToDate(it.redeemDate, AppUtils.DF_dd_MMM_yyyy)

            if (it.redeemFlag) {
                atvOrderStatus.text = getString(R.string.order_placed)
                atvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorGreen))

            } else {
                atvOrderStatus.text = getString(R.string.on_the_way)
                atvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            }

            atvSubTotalAmount.text = String.format(getString(R.string.money_format), it.redeemPoint)
            atvTotalAmount.text = String.format(getString(R.string.money_format), it.redeemPoint)

            // adding item
            val itemModel = MallOrderedItemModel()
            itemModel.imageUrl = it.imageURL
            itemModel.productName = it.productName
            itemModel.productCategory = it.redeemCategory
            itemModel.productQuantity = (it.redeemPoint / it.redeemValue).toInt()
            itemModel.productPrice = it.redeemValue
            itemModel.productConsultant = ""

            mItemsList.add(itemModel)

        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }
        }
    }


    private fun setupAdapter() {
        mItemsAdapter = MallOrderedItemAdapter(mItemsList)

        rvItems.itemAnimator = DefaultItemAnimator()
        rvItems.adapter = mItemsAdapter
    }


}