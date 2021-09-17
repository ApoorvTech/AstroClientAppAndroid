package com.myastrotell.ui.astromall.productdetail

import android.os.Bundle
import com.myastrotell.R
import com.myastrotell.adapters.ProductImagesPagerAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.AppConstants
import com.myastrotell.databinding.ActivityViewProductImageBinding
import kotlinx.android.synthetic.main.activity_product_detail.ciImages
import kotlinx.android.synthetic.main.activity_product_detail.vpImages
import kotlinx.android.synthetic.main.activity_view_product_image.*


class ViewProductImageActivity : BaseActivity<ActivityViewProductImageBinding, BaseViewModel>() {

    private lateinit var mImagesAdapter: ProductImagesPagerAdapter
    private lateinit var mImageList: ArrayList<String>

    private var selectedPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()
        setupImagesAdapter()

    }

    override fun getLayoutId() = R.layout.activity_view_product_image

    override fun getBindingVariable() = 0

    override fun initViewModel() = null

    override fun initVariables() {

    }

    override fun setObservers() {

        aivCancel.setOnClickListener {
            onBackPressed()
        }

    }


    private fun getData() {

        selectedPosition = intent.getIntExtra(AppConstants.KEY_POSITION, 0)
        mImageList = intent.getSerializableExtra(AppConstants.KEY_DATA) as ArrayList<String>

    }


    private fun setupImagesAdapter() {
        mImagesAdapter = ProductImagesPagerAdapter(mImageList)
        vpImages.adapter = mImagesAdapter
        ciImages.setViewPager(vpImages)

        vpImages.currentItem = selectedPosition
    }

}