package com.myastrotell.ui.astrologynews

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.util.Log
import android.view.View
import com.myastrotell.R
import com.myastrotell.base.BaseFragment
import com.myastrotell.base.BaseViewModel
import com.myastrotell.databinding.FragmentAstrologyNewsBinding
import com.myastrotell.pojo.response.ProductDetail
import com.myastrotell.utils.invisible
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.fragment_astrology_news.*
import java.util.*


class AstroNewsFragment : BaseFragment<FragmentAstrologyNewsBinding, BaseViewModel>(), TextToSpeech.OnInitListener {

    var news: ProductDetail? = null

    private var tts: TextToSpeech? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tts = TextToSpeech(context, this)
        sdvImage.setImageURI(news?.productImage)

        val text :StringBuilder= java.lang.StringBuilder("<html><body><p align=\"justify\">")
        val textToAppend="</p></body></html>"

        text.append(news?.productDescription)
        text.append(textToAppend)
        atvDescription.text = Html.fromHtml(text.toString())
        webView.loadData(text.toString(), "text/html", "utf-8");


        val speakout = speakout!!
        val speak = speak!!
        //atvDescription.text = news?.productDescription


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
    }

    override fun getLayoutId() = R.layout.fragment_astrology_news

    override fun getBindingVariable() = 0

    override fun initViewModel() = null

    override fun initVariables() {

    }

    override fun setObservers() {

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
