package com.myastrotell.ui.astromall.productdetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.myastrotell.R
import com.myastrotell.adapters.ProductImagesPagerAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityProductDetailBinding
import com.myastrotell.pojo.response.RedemptionSearchResponse
import com.myastrotell.ui.astromall.AstroMallViewModel
import com.myastrotell.ui.cart.CartActivity
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.layout_toolbar_with_cart.*


class ProductDetailActivity : BaseActivity<ActivityProductDetailBinding, AstroMallViewModel>(),
    View.OnClickListener {

    private lateinit var mImagesAdapter: ProductImagesPagerAdapter
    private lateinit var mImageList: ArrayList<String>

    private var text :StringBuilder= java.lang.StringBuilder("<html><body><p align=\"justify\">")
    private val textToAppend="</p></body></html>"

    private var data: RedemptionSearchResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.product_details)

        getAndSetIntentData()

        setupImagesAdapter()

    }


    override fun onResume() {
        super.onResume()

        viewModel?.setCartItemCount()

        data?.let {
            viewModel?.isAddedToCart(it.goodsId)
        }

    }


    override fun getLayoutId() = R.layout.activity_product_detail

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<AstroMallViewModel>()

    override fun initVariables() {
        mImageList = ArrayList()
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

        viewModel?.isAddedToCart?.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val isAdded = (sender as ObservableBoolean).get()
                if (isAdded)
                    btnAddToCart.gone()
                else
                    btnAddToCart.visible()
            }
        })

        aivBack.setOnClickListener(this)
        flCart.setOnClickListener(this)
        aivViewFullScreenImage.setOnClickListener(this)
        aivMinus.setOnClickListener(this)
        aivPlus.setOnClickListener(this)
        atvLabelProductDetail.setOnClickListener(this)
        btnAddToCart.setOnClickListener(this)


    }


    private fun getAndSetIntentData() {

        data = intent.getParcelableExtra(AppConstants.KEY_DATA)

        data?.let { data ->
            atvProductName.text = data.goodsName
            atvProductDesc.text = data.goodsShortDescription ?: ""
            atvProductPrice.text = (getString(R.string.currency) + "${data.goodsPrice} / piece")



            text.append(data.goodsDescription)
            text.append(textToAppend)
            webview.loadData(text.toString(), "text/html", "utf-8");


            atvProductDetail.text = data.goodsDescription

            //atvProductDetail.text = data.goodsDescription

            mImageList.clear()
            mImageList.add(data.goodsImage)

        }
    }


    private fun setupImagesAdapter() {
        mImagesAdapter = ProductImagesPagerAdapter(mImageList, clickListener = { position ->
            viewImageInFullScreen(position)
        })
        vpImages.adapter = mImagesAdapter
        ciImages.setViewPager(vpImages)
    }


    private fun viewImageInFullScreen(position: Int) {
        if (position >= 0 && position < mImageList.size) {
            val intent = Intent(this, ViewProductImageActivity::class.java)
            intent.putExtra(AppConstants.KEY_POSITION, position)
            intent.putExtra(AppConstants.KEY_DATA, mImageList)
            startActivity(intent)

        }
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

            R.id.aivViewFullScreenImage -> {
                viewImageInFullScreen(vpImages.currentItem)
            }

            R.id.aivMinus -> {
                val quantity = atvQuantity.text.toString().toInt()
                if (quantity > 1) {
                    atvQuantity.text = (quantity - 1).toString()
                }
            }

            R.id.aivPlus -> {
                val quantity = atvQuantity.text.toString().toInt()
                if (quantity < 100) {
                    atvQuantity.text = (quantity + 1).toString()
                }
            }

            R.id.atvLabelProductDetail -> {



                if (webview.isVisible) {
                    webview.gone()
                    aivProductDetailArrow.animate().rotation(90f).duration = 250

                } else {
                    webview.visible()
                    aivProductDetailArrow.animate().rotation(270f).duration = 250
                }
//                if (atvProductDetail.isVisible) {
//                    atvProductDetail.gone()
//                    aivProductDetailArrow.animate().rotation(90f).duration = 250
//
//                } else {
//                    atvProductDetail.visible()
//                    aivProductDetailArrow.animate().rotation(270f).duration = 250
//                }
            }

            R.id.btnAddToCart -> {
                if (isUserLoggedIn()) {

                    data?.let {
                        viewModel?.addItemToCart(it, atvQuantity.text.toString().toInt())
                        btnAddToCart.gone()
                        showShortToast(getString(R.string.added_to_cart))
                    }

                } else {
                    showLoginDialog()
                }
            }

        }

    }

}