package com.myastrotell.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.NotificationListAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.databinding.ActivityNotificationsBinding
import com.myastrotell.pojo.response.Notifications
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class NotificationsActivity : BaseActivity<ActivityNotificationsBinding, NotificationsViewModel>(),
    View.OnClickListener {

    private lateinit var mNotificationsAdapter: NotificationListAdapter
    private lateinit var mNotificationList: ArrayList<Notifications>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.notifications)

        setUpAdapter()

        viewModel?.getNotifications()

    }


    override fun getLayoutId() = R.layout.activity_notifications

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<NotificationsViewModel>()

    override fun initVariables() {
        mNotificationList = ArrayList()

    }

    override fun setObservers() {

        viewModel?.notificationsData?.observe(this, Observer {
            hideProgressBar()

            mNotificationList.clear()

            it.data?.response?.let { list ->
                mNotificationList.addAll(list)
                mNotificationsAdapter.notifyDataSetChanged()
            }

            if (mNotificationList.size > 0) {
                atvNoDataPlaceholder.gone()
            } else {
                atvNoDataPlaceholder.visible()
            }

        })

        aivBack.setOnClickListener(this)
    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        if (responseModel?.apiRequestCode == ApiRequestCodes.NOTIFICATIONS &&
            responseModel.code == ApiStatusCodes.NO_DATA_FOUND
        ) {

            if (mNotificationList.size > 0) {
                atvNoDataPlaceholder.gone()
            } else {
                atvNoDataPlaceholder.visible()
            }

        } else {
            super.handleApiErrorResponse(responseModel)
        }
    }


    private fun setUpAdapter() {
        mNotificationsAdapter = NotificationListAdapter(mNotificationList)
        rvNotifications.itemAnimator = DefaultItemAnimator()
        rvNotifications.adapter = mNotificationsAdapter

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }
        }
    }


}