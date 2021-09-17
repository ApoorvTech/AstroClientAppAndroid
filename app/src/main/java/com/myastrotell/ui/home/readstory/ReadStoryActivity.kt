package com.myastrotell.ui.home.readstory

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.databinding.ActivityReadStoryBinding
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.fragment_astrology_news.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*


class ReadStoryActivity : BaseActivity<ActivityReadStoryBinding, ReadStoryViewModel>(),
    View.OnClickListener {

    private var text :StringBuilder= java.lang.StringBuilder("<html><body><p align=\"justify\">")
    private val textToAppend="</p></body></html>"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        atvTitle.text = getString(R.string.our_story)
        viewModel?.getReadStory()

    }






    override fun getLayoutId() = R.layout.activity_read_story

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<ReadStoryViewModel>()

    override fun initVariables() {

    }

    override fun setObservers() {

        viewModel?.readStoryLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.productDetails?.let { details ->
                if (details.isNotEmpty()) {

                text.append(details[0].productDescription)
                text.append(textToAppend)



                    binding.layoutStory.atvDescription.text = Html.fromHtml(text.toString())

                    webView.loadData(text.toString(), "text/html", "utf-8");

                    //binding.layoutStory.atvDescription.text = details[0].productDescription
                    //binding.layoutStory.atvDescription.invalidate()
                    sdvImage.setImageURI(details[0].productImage)
                }
            }

        })

        aivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }
        }
    }

}