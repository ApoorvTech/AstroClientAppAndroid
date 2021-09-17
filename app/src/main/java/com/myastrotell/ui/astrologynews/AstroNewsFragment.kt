package com.myastrotell.ui.astrologynews

import android.os.Bundle
import android.text.Html
import android.view.View
import com.myastrotell.R
import com.myastrotell.base.BaseFragment
import com.myastrotell.base.BaseViewModel
import com.myastrotell.databinding.FragmentAstrologyNewsBinding
import com.myastrotell.pojo.response.ProductDetail
import kotlinx.android.synthetic.main.fragment_astrology_news.*


class AstroNewsFragment : BaseFragment<FragmentAstrologyNewsBinding, BaseViewModel>() {

    var news: ProductDetail? = null
    private var text :StringBuilder= java.lang.StringBuilder("<html><body><p align=\"justify\">")
    private val textToAppend="</p></body></html>";

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sdvImage.setImageURI(news?.productImage)
        text.append(news?.productDescription)
        text.append(textToAppend)
        atvDescription.text = Html.fromHtml(text.toString())
        webView.loadData(text.toString(), "text/html", "utf-8");

        //atvDescription.text = news?.productDescription

    }


    override fun getLayoutId() = R.layout.fragment_astrology_news

    override fun getBindingVariable() = 0

    override fun initViewModel() = null

    override fun initVariables() {

    }

    override fun setObservers() {

    }

}