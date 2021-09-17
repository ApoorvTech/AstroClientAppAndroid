package com.myastrotell.ui.astromall.items

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.MallItemsListAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.databinding.ActivityAstroMallItemsBinding
import com.myastrotell.interfaces.ProductItemClickListener
import com.myastrotell.pojo.response.RedemptionSearchResponse
import com.myastrotell.ui.astromall.AstroMallViewModel
import com.myastrotell.ui.astromall.productdetail.ProductDetailActivity
import com.myastrotell.ui.cart.CartActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_astro_mall_items.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar_with_cart.*


class AstroMallItemsActivity : BaseActivity<ActivityAstroMallItemsBinding, AstroMallViewModel>(),
    View.OnClickListener {

    private lateinit var mItemsAdapter: MallItemsListAdapter
    private lateinit var mItemsList: ArrayList<RedemptionSearchResponse>

    private lateinit var mCartItemsList: ArrayList<CartEntity>

    private lateinit var categoryId: String
    private lateinit var categoryName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        setUpAdapter()

        viewModel?.getCartItems()
        viewModel?.getMallItems(categoryName)

    }


    override fun onResume() {
        super.onResume()
        viewModel?.setCartItemCount()

    }

    override fun getLayoutId() = R.layout.activity_astro_mall_items


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<AstroMallViewModel>()


    override fun initVariables() {
        mCartItemsList = ArrayList()
        mItemsList = ArrayList()

    }


    private fun getData() {
        categoryId = intent?.getStringExtra(AppConstants.KEY_ID) ?: ""

        categoryName = intent?.getStringExtra(AppConstants.KEY_TITLE) ?: ""

        atvTitle.text = categoryName

    }


    override fun setObservers() {

        viewModel?.cartItemCount?.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {

            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val count = (sender as ObservableInt).get()

                atvCartItemCount.text = count.toString()

                if (count > 0)
                    atvCartItemCount.visible()
                else
                    atvCartItemCount.gone()

            }
        })



        viewModel?.cartItemsLiveData?.observe(this, Observer {

            mCartItemsList.clear()

            it?.let { list ->
                mCartItemsList.addAll(list)
            }

        })


        viewModel?.mallItemsLiveData?.observe(this, Observer {
            hideProgressBar()

            val list = ArrayList<RedemptionSearchResponse>()

            it.data?.forEach { item ->
//                var isAdded = false
//                for (cartItem in mCartItemsList) {
//                    if (item.goodsId.equals(cartItem.productId, true)) {
//                        isAdded = true
//                        break
//                    }
//                }
//                item.isAddedToCart = isAdded

                list.add(item)

            }

            mItemsAdapter.updateList(list)

            if (mItemsList.size > 0) {
                atvNoDataPlaceholder.gone()
            } else {
                atvNoDataPlaceholder.visible()
            }

        })


        aetSearch.doAfterTextChanged {
            mItemsAdapter.filter.filter(it?.trim().toString())
        }


        aivBack.setOnClickListener(this)
        flCart.setOnClickListener(this)

    }


    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        if (responseModel?.apiRequestCode == ApiRequestCodes.REDEMPTION_SEARCH &&
            responseModel.code == ApiStatusCodes.NO_DATA_FOUND
        ) {

            if (mItemsList.size > 0) {
                atvNoDataPlaceholder.gone()
            } else {
                atvNoDataPlaceholder.visible()
            }

        } else {
            super.handleApiErrorResponse(responseModel)
        }
    }


    /**
     * Setting Items Adapter
     */
    private fun setUpAdapter() {

        mItemsAdapter = MallItemsListAdapter(mItemsList, object : ProductItemClickListener {

            override fun onAddToCartClicked(position: Int) {
                if (isUserLoggedIn()) {
                    viewModel?.addItemToCart(mItemsList[position], 1)
                    showShortToast(getString(R.string.added_to_cart))

                } else {
                    showLoginDialog()
                }
            }

            override fun onItemClicked(position: Int) {
                val intent = Intent(this@AstroMallItemsActivity, ProductDetailActivity::class.java)
                intent.putExtra(AppConstants.KEY_POSITION, position)
                intent.putExtra(AppConstants.KEY_DATA, mItemsList[position])
                startActivityForResult(intent, 10)
            }

        }, onSearchFinished = { size ->
            if (size > 0) {
                atvNoDataPlaceholder.gone()
            } else {
                atvNoDataPlaceholder.visible()
            }
        })

        rvItems.itemAnimator = DefaultItemAnimator()
        rvItems.adapter = mItemsAdapter

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.flCart -> {
                if (isUserLoggedIn()) {

                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)

                } else {
                    showLoginDialog()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            10 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // update item here

                }
            }
        }
    }


    override fun showProgressBar() {
        progressBar?.visible()
    }


    override fun hideProgressBar() {
        progressBar?.gone()
    }


}