package com.myastrotell.adapters

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.response.HororsopeDetails
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.list_item_daily_horoscope.view.*
import java.util.*
import kotlin.collections.ArrayList


class HoroscopeListAdapter(
    private val mHororscopeList: ArrayList<HororsopeDetails>
) : RecyclerView.Adapter<HoroscopeListAdapter.HoroscopeViewHolder>() , TextToSpeech.OnInitListener{

    var context: Context? = null
    private var tts: TextToSpeech? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        context = parent.context
        return HoroscopeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_daily_horoscope, parent, false)
        )
    }


    override fun getItemCount() = mHororscopeList.size


    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {
        tts = TextToSpeech(context, this)
        holder.atvTitle.text = mHororscopeList[position].productTitle

        // holder.atvDescription.text = mHororscopeList[position].productDescription

        val text: StringBuilder = java.lang.StringBuilder("<html><body><p align=\"justify\">")
        val textToAppend = "</p></body></html>"

        text.append(mHororscopeList[position].productDescription)
        text.append(textToAppend)

        holder.webView.loadData(
            text.toString(), "text/html", "utf-8"

        )
        holder.speakout!!.setOnClickListener {

            speakOut(mHororscopeList[position].productDescription.toString());
            holder.speak!!.visible()
            holder.speakout!!.gone()
        }


        holder.speak!!.setOnClickListener {
            holder.speakout!!.visible()
            holder.speak!!.gone()
            tts!!.stop()

        }

    }


    inner class HoroscopeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvTitle = itemView.atvTitle!!
        val speakout = itemView.speakout!!
        val speak = itemView.speak!!
        val atvDescription = itemView.atvDescription!!
        val webView = itemView.webView!!

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
