package com.myastrotell.ui.astromall.categories

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.ProductCategoriesAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityAstroMallCategoriesBinding
import com.myastrotell.pojo.response.ProductDetail
import com.myastrotell.ui.astromall.AstroMallViewModel
import com.myastrotell.ui.astromall.items.AstroMallItemsActivity
import com.myastrotell.ui.cart.CartActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_astro_mall_categories.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar_with_cart.*


class AstroMallCategoriesActivity :
    BaseActivity<ActivityAstroMallCategoriesBinding, AstroMallViewModel>(), View.OnClickListener {

    private lateinit var mCategoriesAdapter: ProductCategoriesAdapter
    private lateinit var mCategoriesList: ArrayList<ProductDetail>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.shop_at_astro_mall)

        setUpAdapter()

        viewModel?.getMallCategories()

    }

    override fun onResume() {
        super.onResume()
        viewModel?.setCartItemCount()
    }


    override fun getLayoutId() = R.layout.activity_astro_mall_categories

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<AstroMallViewModel>()

    override fun initVariables() {
        mCategoriesList = ArrayList()

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


        viewModel?.mallCategoryLiveData?.observe(this, Observer {
            hideProgressBar()
            mCategoriesList.clear()
            it.data?.productDetails?.let { list ->
                mCategoriesList.addAll(list)
            }
            mCategoriesAdapter.notifyDataSetChanged()

        })

        aivBack.setOnClickListener(this)
        flCart.setOnClickListener(this)

    }


    /**
     * Setting Categories Adapter
     */
    private fun setUpAdapter() {

        mCategoriesAdapter =
            ProductCategoriesAdapter(mCategoriesList, mClickListener = { position ->
                // handle item click here
                mCategoriesList[position]?.let{ data ->
                    val intent = Intent(this, AstroMallItemsActivity::class.java)
                    intent.putExtra(AppConstants.KEY_ID, position.toString())
                    intent.putExtra(AppConstants.KEY_TITLE, data.productTitle)
                    startActivity(intent)
                }
            })

        rvCategories.itemAnimator = DefaultItemAnimator()
        rvCategories.adapter = mCategoriesAdapter

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

    override fun showProgressBar() {
        progressBar?.visible()
    }


    override fun hideProgressBar() {
        progressBar?.gone()
    }

}