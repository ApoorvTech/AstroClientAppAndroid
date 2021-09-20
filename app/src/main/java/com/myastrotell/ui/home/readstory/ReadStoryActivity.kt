package com.myastrotell.ui.home.readstory

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.databinding.ActivityReadStoryBinding
import com.myastrotell.utils.getViewModel
import com.myastrotell.utils.invisible
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.fragment_astrology_news.*
import kotlinx.android.synthetic.main.fragment_astrology_news.sdvImage
import kotlinx.android.synthetic.main.fragment_astrology_news.speak
import kotlinx.android.synthetic.main.fragment_astrology_news.speakout
import kotlinx.android.synthetic.main.layout_toolbar_primary.*
import java.util.*


class ReadStoryActivity : BaseActivity<ActivityReadStoryBinding, ReadStoryViewModel>(),
    View.OnClickListener, TextToSpeech.OnInitListener  {

    private var text :StringBuilder= java.lang.StringBuilder("<html><body><p align=\"justify\">")
    private val textToAppend="</p></body></html>";
    private var tts: TextToSpeech? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        atvTitle.text = getString(R.string.our_story)
        viewModel?.getReadStory()
        tts = TextToSpeech(this, this)

    }

    override fun getLayoutId() = R.layout.activity_read_story

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<ReadStoryViewModel>()

    override fun initVariables() {

    }

    override fun setObservers() {

        viewModel?.readStoryLiveData?.observe(this, Observer {
            hideProgressBar()
            val speakout = speakout!!
            val speak = speak!!
            it?.data?.productDetails?.let { details ->
                if (details.isNotEmpty()) {

                text.append(details[0].productDescription)
                text.append(textToAppend)

                    binding.layoutStory.atvDescription.text = Html.fromHtml(text.toString())

                    webView.loadData(text.toString(), "text/html", "utf-8");
//                    webView.setBackgroundResource(R.drawable.fadeimageicons);
//                    webView.setBackgroundColor(Color.TRANSPARENT);
                    val plain = Html.fromHtml(text.toString()).toString()
                    speakout!!.setOnClickListener {

                        speakOut(plain.toString());
                        speak!!.visible()
                        speakout!!.invisible()
                    }


                    speak!!.setOnClickListener {
                        speakout!!.visible()
                        speak!!.invisible()
                        tts!!.stop()

                    }

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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts


            val result = tts!!.setLanguage(Locale("en", "IN"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {

            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }
    private fun speakOut(status: String ) {
        val text = status
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)

    }

}
