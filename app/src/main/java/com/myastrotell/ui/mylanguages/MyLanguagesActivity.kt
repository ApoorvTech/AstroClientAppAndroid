package com.myastrotell.ui.mylanguages

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.myastrotell.R
import com.myastrotell.adapters.MyLanguagesAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.databinding.ActivityMyLaguagesBinding
import com.myastrotell.interfaces.MyLanguageItemClickListener
import com.myastrotell.pojo.response.KeyDataResponse
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_my_laguages.*
import kotlinx.android.synthetic.main.layout_toolbar_with_cart.*

class MyLanguagesActivity : BaseActivity<ActivityMyLaguagesBinding, MyLanguagesViewModel>(),
    View.OnClickListener {

    private lateinit var mLanguagesAdapter: MyLanguagesAdapter
    private lateinit var mLanguageList: ArrayList<KeyDataResponse>
    private lateinit var mSelectedLanguageList: ArrayList<SelectedLanguagesEntity>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atvTitle.text = getString(R.string.my_languages)

        setUpAdapter()

        viewModel?.getSelectedLanguages()

    }

    override fun getLayoutId() = R.layout.activity_my_laguages


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<MyLanguagesViewModel>()


    override fun initVariables() {
        mLanguageList = ArrayList()
        mSelectedLanguageList = ArrayList()
    }


    override fun setObservers() {
        viewModel?.selectedLanguagesLiveData?.observe(this, Observer {
            hideProgressBar()

            mSelectedLanguageList.clear()

            it?.let { list ->
                mSelectedLanguageList.addAll(list)
            }

            viewModel?.getLanguageList()

        })


        viewModel?.languageListLiveData?.observe(this, Observer {
            hideProgressBar()

            mLanguageList.clear()

            it?.forEach { item ->
                item.isSelected = false

                for (lang in mSelectedLanguageList) {
                    if (item.value.toLowerCase().equals(lang.value?.toLowerCase(), true)) {
                        item.isSelected = true
                        break
                    }
                }
                mLanguageList.add(item)
            }

            mLanguagesAdapter.notifyDataSetChanged()

        })


        aivBack.setOnClickListener(this)
        btnDone.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.btnDone -> {
                var isValidated = false
                for (lang in mLanguageList) {
                    if (lang.isSelected) {
                        isValidated = true
                        break
                    }
                }

                if (isValidated) {
                    viewModel?.updatedSelectedLanguage(mLanguageList)

                    showShortToast(getString(R.string.languages_updated))
                    onBackPressed()

                } else {
                    showShortToast(getString(R.string.please_select_at_least_one_language))
                }
            }
        }
    }

    /**
     * Setting Items Adapter
     */
    private fun setUpAdapter() {
        mLanguagesAdapter = MyLanguagesAdapter(mLanguageList, object : MyLanguageItemClickListener {

            override fun onItemCheck(position: Int) {
                mLanguageList[position].isSelected = !mLanguageList[position].isSelected
                mLanguagesAdapter.notifyItemChanged(position)
            }

        })

        rvMyLanguages.itemAnimator = DefaultItemAnimator()
        rvMyLanguages.adapter = mLanguagesAdapter
    }


}