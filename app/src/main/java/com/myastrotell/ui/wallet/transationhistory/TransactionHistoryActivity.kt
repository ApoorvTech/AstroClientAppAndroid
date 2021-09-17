package com.myastrotell.ui.wallet.transationhistory

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.TransactionHistoryAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.databinding.ActivityTransactionHistoryBinding
import com.myastrotell.pojo.response.WalletTransactionResponse
import com.myastrotell.ui.wallet.WalletActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_transaction_history.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*
import kotlinx.android.synthetic.main.layout_wallet_recharge_header.*


class TransactionHistoryActivity :
    BaseActivity<ActivityTransactionHistoryBinding, TransactionHistoryViewModel>(),
    View.OnClickListener {

    private lateinit var mTransactionAdapter: TransactionHistoryAdapter
    private lateinit var mTransactionList: ArrayList<WalletTransactionResponse>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.wallet_transactions)

        setUpAdapter()

        viewModel?.getTransactionHistory()

    }


    override fun onResume() {
        super.onResume()

        atvBalance.text = getString(R.string.currency)
        atvBalance.append(getWalletBalance())

    }


    override fun getLayoutId() = R.layout.activity_transaction_history

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<TransactionHistoryViewModel>()

    override fun initVariables() {
        mTransactionList = ArrayList()

    }


    override fun setObservers() {
        viewModel?.transactionHistoryLiveData?.observe(this, Observer {
            hideProgressBar()

            mTransactionList.clear()

            it?.data?.let { data ->
                mTransactionList.addAll(data)
                mTransactionAdapter.notifyDataSetChanged()
            }

            if (mTransactionList.isEmpty())
                atvNoDataPlaceholder.visible()
            else
                atvNoDataPlaceholder.gone()

        })


        aivBack.setOnClickListener(this)
        atvRecharge.setOnClickListener(this)

    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        if (responseModel?.apiRequestCode == ApiRequestCodes.PASSBOOK &&
            responseModel.code == ApiStatusCodes.NO_DATA_FOUND
        ) {

            if (mTransactionList.isEmpty())
                atvNoDataPlaceholder.visible()
            else
                atvNoDataPlaceholder.gone()

        } else {
            super.handleApiErrorResponse(responseModel)
        }
    }


    private fun setUpAdapter() {
        mTransactionAdapter = TransactionHistoryAdapter(mTransactionList)
        rvTransaction.itemAnimator = DefaultItemAnimator()
        rvTransaction.adapter = mTransactionAdapter

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvRecharge -> {
                val intent = Intent(this, WalletActivity::class.java)
                startActivity(intent)
            }
        }
    }


}