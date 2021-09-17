package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.response.Feature
import kotlinx.android.synthetic.main.list_item_sun_sign.view.*


class SunSignListAdapter(
    private val mSunSignList: ArrayList<Feature>,
    private val mClickListener: ((Int) -> Unit)? = null,
    private val showSelection: Boolean = false
) : RecyclerView.Adapter<SunSignListAdapter.SunSignViewHolder>() {


    private var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SunSignViewHolder {
        context = parent.context
        return SunSignViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_sun_sign, parent, false)
        )
    }


    override fun getItemCount() = mSunSignList.size


    override fun onBindViewHolder(holder: SunSignViewHolder, position: Int) {

        holder.atvName.text = mSunSignList[position].featureName ?: ""
        holder.atvDate.text = mSunSignList[position].layout ?: ""

        holder.clMain.isSelected = mSunSignList[position].isSelected

        val iconUrl = mSunSignList[position].featureIcon

//        if(mSunSignList[position].isSelected){
//            iconUrl = "$iconUrl-1"
//        }

        holder.sdvImage.setImageURI(iconUrl)


        /*
        when (mSunSignList[position].featureName.toString().toLowerCase(Locale.ENGLISH)) {
            SunSigns.ARIES.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.aries_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.aries)
            }
            SunSigns.TAURUS.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.taurus_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.taurus)
            }
            SunSigns.GEMINI.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.gemini_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.gemini)
            }
            SunSigns.CANCER.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.cancer_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.cancer)
            }
            SunSigns.LEO.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.leo_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.leo)
            }
            SunSigns.VIRGO.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.virgo_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.virgo)
            }
            SunSigns.LIBRA.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.libra_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.libra)
            }
            SunSigns.SCORPIO.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.scorpio_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.scorpio)
            }
            SunSigns.SAGITTARIUS.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.sagittarius_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.sagittarius)
            }
            SunSigns.CAPRICORN.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.capricorn_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.capricorn)
            }
            SunSigns.AQUARIUS.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.aquarius_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.aquarius)
            }
            SunSigns.PISCES.value -> {
                if (showSelection && mSunSignList[position].isSelected)
                    holder.sdvImage.setActualImageResource(R.drawable.piscces_1)
                else
                    holder.sdvImage.setActualImageResource(R.drawable.piscces)
            }
        }
        */

    }


    inner class SunSignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sdvImage = itemView.sdvImage!!
        val atvName = itemView.atvName!!
        val atvDate = itemView.atvDate!!
        val clMain = itemView.clMain!!

        init {
            itemView.setOnClickListener { mClickListener?.invoke(adapterPosition) }
        }

    }

}