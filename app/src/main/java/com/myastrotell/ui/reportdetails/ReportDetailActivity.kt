package com.myastrotell.ui.reportdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.myastrotell.BuildConfig
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.ApiEndPoints
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityReportDetailBinding
import com.myastrotell.pojo.response.ProductBillingDetails
import com.myastrotell.ui.orderhistory.OrderHistoryViewModel
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_report_detail.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class ReportDetailActivity : BaseActivity<ActivityReportDetailBinding, OrderHistoryViewModel>(),
    View.OnClickListener {


    private var orderData: ProductBillingDetails?  = null

    private var isOrderCompleted = false

    private var reportUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        orderData?.let {
            viewModel?.getOrderDetails(it.id)
        }

    }


    override fun getLayoutId() = R.layout.activity_report_detail


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<OrderHistoryViewModel>()


    override fun initVariables() {

    }


    private fun getData() {
        atvTitle.text = getString(R.string.report_detail)

        orderData = intent?.getParcelableExtra(AppConstants.KEY_DATA)

        orderData?.let {

            astrologerName.text = it.productName

            sdvImage.setImageURI(it.imageURL)

            isOrderCompleted = it.redeemFlag

            if (isOrderCompleted) {
                stausValueTV.text = getString(R.string.completed)
                stausValueTV.setTextColor(ContextCompat.getColor(this, R.color.colorGreen))

            } else {
                stausValueTV.text = getString(R.string.report_in_queue)
                stausValueTV.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
            }

            val dateTime = AppUtils.timeStampToDate(it.redeemDate, "dd MMM yyyy, hh:mm aa")
            dateTV.text = dateTime

            atvReportPrice.text =
                ("${String.format(getString(R.string.money_format), it.redeemPoint)}/report")

            answerLanguageTV.text = ("${getString(R.string.answer_language)} English")

        }

    }


    override fun setObservers() {
        viewModel?.orderDetailsLiveData?.observe(this, Observer {
            hideProgressBar()

            clMain.visible()

            it?.data?.let { data ->

                data.deliveryAddress?.let { values ->
                    if (values.contains("|")) {

                        val list = values.split("|")

                        val name = list[0]
                        val number = list[1]
                        val dob = list[2]
                        val pob = list[3]
                        val maritalStatus = list[4]
                        val occupation = list[5]
                        val reportType = list[6]

                        clientNameValueTV.text = name
                        clientMobileValueTV.text = number
                        clientDobValueTV.text = dob
                        clientPobValueTV.text = pob
                        clientMaritalValueTV.text = maritalStatus
                        clientOccupationValueTV.text = occupation

                        reportTypeTitle.text = reportType

                        if (isOrderCompleted) {

                            reportStatusMsg.text = getString(R.string.view_report)

                            var reportId = list[list.size - 1]

                            // getting report file id
                            if (reportId.contains(",")) {
                                val idList = reportId.split(",")
                                reportId = idList[idList.size - 1]
                            }

                            reportUrl =
                                (BuildConfig.CHAT_MACHINE_BASE_URL + ApiEndPoints.DOWNLOAD_REPORT + reportId)

                        }
                    }
                }
            }
        })

        aivBack.setOnClickListener(this)
        reportStatusMsg.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.reportStatusMsg -> {
                if (!reportUrl.isNullOrBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(reportUrl)
                    startActivity(intent)
                }
            }
        }
    }


}