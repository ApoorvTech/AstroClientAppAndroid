package com.myastrotell.ui.home.watchstory

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.databinding.ActivityWatchStoryBinding
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.activity_watch_story.*
import kotlinx.android.synthetic.main.layout_progressbar.*


class WatchStoryActivity : BaseActivity<ActivityWatchStoryBinding, WatchStoryViewModel>(),
    View.OnClickListener {

    private var dataUrl: String? = null

    private var text :StringBuilder= java.lang.StringBuilder("<html><body><p align=\"justify\">")
    private val textToAppend="</p></body></html>"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel?.getWatchStory()

    }


    override fun getLayoutId() = R.layout.activity_watch_story

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<WatchStoryViewModel>()

    override fun initVariables() {

    }

    override fun setObservers() {

        viewModel?.watchStoryLiveData?.observe(this, Observer {
            hideProgressBar()
            it?.data?.productDetails?.let { story ->
                if (story.isNotEmpty()) {
                    atvVideoTitle.text = story[0].productTitle
                    atvVideoDescription.text = story[0].productDescription


                    text.append(story[0].productDescription)
                    text.append(textToAppend)
                    webView.loadData(text.toString(), "text/html", "utf-8");

                    setUpWebViewVideoPlayer(story[0].productVideo)
                    loadVideo()
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


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebViewVideoPlayer(videoUrl: String) {

        val webSettings = wvPlayer.settings
        webSettings.javaScriptEnabled = true
        wvPlayer.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        wvPlayer.settings.loadWithOverviewMode = true
        wvPlayer.settings.useWideViewPort = true
        wvPlayer.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        wvPlayer.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }


        var newVideoUrl = videoUrl
        if (videoUrl.contains("youtube.com/")) {
            newVideoUrl = videoUrl.replace("watch?v=", "embed/", true)

        } else if (videoUrl.contains("youtu.be/")) {
            val arr = videoUrl.split("youtu.be/")
            if (arr.isNotEmpty()) {
                val id = arr[arr.size - 1]
                newVideoUrl = ("https://youtube.com/embed/$id")
            }
        }

        dataUrl =
            "<html>" +
                    "<body style=\"margin:0px;padding:0px;overflow:hidden\">" +
                    "<iframe style=\"overflow:hidden;height:100%;width:100%\" width=\"100%\" height=\"100%\" src=\"" +
                    newVideoUrl + "\" frameborder=\"0\" allowfullscreen=\"true\" webkitallowfullscreen=\"true\"/>" +
                    "</body>" +
                    "</html>"

    }


    private fun loadVideo() {
        wvPlayer.loadData(dataUrl ?: "", "text/html", "utf-8")
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        loadVideo()
        super.onConfigurationChanged(newConfig)
    }


    override fun showProgressBar() {
        progressBar?.visible()
    }


    override fun hideProgressBar() {
        progressBar?.gone()
    }

}