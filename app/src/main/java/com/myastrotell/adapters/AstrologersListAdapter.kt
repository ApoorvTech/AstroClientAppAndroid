package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.IntegerRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.data.AstrologersListType
import com.myastrotell.interfaces.AstrologerItemClickListener
import com.myastrotell.pojo.response.AstrologerListResponse
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.list_item_astrologers.view.*


class AstrologersListAdapter(
    private val mAstrologersList: ArrayList<AstrologerListResponse>,
    private val type: String = "",
    private val mClickListener: AstrologerItemClickListener? = null,
    private val onSearchFinished: (Int) -> Unit
) : RecyclerView.Adapter<AstrologersListAdapter.AstrologerViewHolder>(), Filterable {

    private var context: Context? = null
    private val mList = ArrayList<AstrologerListResponse>()
    private var mFilter: MyFilter? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AstrologerViewHolder {
        context = parent.context
        return AstrologerViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_astrologers, parent, false)
        )
    }


    override fun getItemCount() = mAstrologersList.size


    override fun onBindViewHolder(holder: AstrologerViewHolder, position: Int) {

        mAstrologersList[position].let {

            val imageUrl = it.goodsImage.toString()
            holder.sdvImage.setImageURI(imageUrl)

            holder.atvName.text = it.goodsName.toString()

            holder.atvSkills.text = it.goodsShortDescription.toString()
            holder.atvLanguages.text = it.goodsLanguage.toString()

            holder.atvExperience.text = ("Exp: ")
            holder.atvExperience.append(it.goodsAttribute.toString())
            holder.atvExperience.append(" Years")

            holder.ratingBar.rating = it.goodsAvgRating ?: 0f

            if(it.goodsTotalRating!=null) {
                holder.atvTotalPersonAttended.text = it.goodsTotalRating.toString()
                holder.atvTotalPersonAttended.append(" ")
                holder.atvTotalPersonAttended.append("total")
            }
            else {
                holder.atvTotalPersonAttended.text = "0"
            }



            holder.atvFees.text = context?.getString(R.string.currency)
            holder.atvFees.append(String.format("%.2f", it.goodsPrice ?: 0.0))


            if (type.equals(AstrologersListType.REPORT.value, false)) {
                // User can request for a report anytime
                // regardless astrologer being online/offline

                holder.atvAction.backgroundTintList =
                    ContextCompat.getColorStateList(context!!, R.color.colorGreen)

                holder.atvAction.isEnabled = true

                holder.aivBell.gone()
                holder.atvWaitTime.gone()

            } else {
                if (it.goodsSale == 1) {
                    // Online

                    if (it.isBusy && !type.equals(AstrologersListType.REPORT.value, false)) {
                        // busy
                        holder.aivBell.visible()
                        holder.atvWaitTime.visible()

                        holder.atvWaitTime.text = context?.getString(R.string.currently_busy)
                        holder.atvWaitTime.setTextColor(
                            ContextCompat.getColor(context!!, R.color.colorRed)
                        )

                        holder.atvAction.backgroundTintList =
                            ContextCompat.getColorStateList(context!!, R.color.colorRed)

                        holder.atvAction.isEnabled = false

                    } else {
                        // available
                        holder.atvWaitTime.gone()
                        holder.aivBell.gone()

                        holder.atvAction.backgroundTintList =
                            ContextCompat.getColorStateList(context!!, R.color.colorGreen)

                        holder.atvAction.isEnabled = true

                    }

                } else {
                    // offline
                    holder.aivBell.visible()
                    holder.atvWaitTime.visible()

                    holder.atvWaitTime.text = context?.getString(R.string.currently_offline)
                    holder.atvWaitTime.setTextColor(
                        ContextCompat.getColor(context!!, R.color.colorGrayText)
                    )

                    holder.atvAction.backgroundTintList =
                        ContextCompat.getColorStateList(context!!, R.color.colorGray)

                    holder.atvAction.isEnabled = false

                }
            }

            when (type) {
                AstrologersListType.CHAT.value -> {
                    holder.atvAction.text = context?.getString(R.string.chat)
                    holder.atvFees.append("/min")
                }

                AstrologersListType.CALL.value -> {
                    holder.atvAction.text = context?.getString(R.string.call)
                    holder.atvFees.append("/min")
                }

                AstrologersListType.REPORT.value -> {
                    holder.atvAction.text = context?.getString(R.string.get_report)
                    holder.atvFees.append("/report")
                }
            }

        }

    }

    inner class AstrologerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sdvImage = itemView.sdvImage!!
        val atvName = itemView.atvName!!
        val atvSkills = itemView.atvSkills!!
        val atvLanguages = itemView.atvLanguages!!
        val atvExperience = itemView.atvExperience!!
        val ratingBar = itemView.ratingBar!!
        val atvTotalPersonAttended = itemView.atvTotalPersonAttended!!
        val atvFees = itemView.atvFees!!
        val aivBell = itemView.aivBell!!
        val atvAction = itemView.atvAction!!
        val atvWaitTime = itemView.atvWaitTime!!

        init {
            aivBell.setOnClickListener { mClickListener?.onBellIconClicked(adapterPosition) }

            atvAction.setOnClickListener {
                if (mAstrologersList[adapterPosition].goodsSale == 1 ||
                    type.equals(AstrologersListType.REPORT.value, false)
                ) {
                    mClickListener?.onActionClicked(adapterPosition)
                }
            }

            itemView.setOnClickListener { mClickListener?.onItemClicked(adapterPosition) }
        }

    }


    fun updateList(list: ArrayList<AstrologerListResponse>?) {

//        list?.sortBy {
//            it.sequence?.toInt()
//        }
        list?.let {
            mAstrologersList.clear()
            mList.clear()

            mAstrologersList.addAll(it)
            mList.addAll(it)
            notifyDataSetChanged()
        }
    }


    override fun getFilter(): Filter {
        if (mFilter == null)
            mFilter = MyFilter()

        return mFilter!!
    }


    private inner class MyFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val query = constraint?.trim().toString().toLowerCase()

            val resultList = ArrayList<AstrologerListResponse>()

            if (query.isBlank()) {
                resultList.addAll(mList)

            } else {
                mList.forEach {
                    if (it.goodsName.toString().toLowerCase().contains(query) ||
                        it.goodsShortDescription.toString().toLowerCase().contains(query) ||
                        it.goodsLanguage.toString().toLowerCase().contains(query)
                    ) {
                        resultList.add(it)
                    }
                }
            }

            val filterResult = FilterResults()
            filterResult.values = resultList
            filterResult.count = resultList.size

            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            mAstrologersList.clear()
            mAstrologersList.addAll(results?.values as ArrayList<AstrologerListResponse>)
            notifyDataSetChanged()

            onSearchFinished.invoke(mAstrologersList.size)
        }

    }


    /**
     * method to sort list
     */
    fun sortList(@IntegerRes selectedSortId: Int, @IntegerRes previousSortId: Int) {
        when (selectedSortId) {
            R.id.atvExperienceHTL -> {
                if (previousSortId != R.id.atvExperienceHTL) {
                    mAstrologersList.clear()

                    val newList = ArrayList<AstrologerListResponse>()
                    newList.addAll(mList)

                    for (i in newList.indices) {
                        var pos = 0
                        for (j in 1 until newList.size) {
                            if (newList[j].goodsAttribute.toString()
                                    .toDouble() > newList[pos].goodsAttribute.toString().toDouble()
                            ) {
                                pos = j
                            }
                        }

                        mAstrologersList.add(newList.removeAt(pos))
                    }

                    notifyDataSetChanged()
                }
            }

            R.id.atvExperienceLTH -> {
                if (previousSortId != R.id.atvExperienceLTH) {
                    mAstrologersList.clear()

                    val newList = ArrayList<AstrologerListResponse>()
                    newList.addAll(mList)

                    for (i in newList.indices) {
                        var pos = 0
                        for (j in 1 until newList.size) {
                            if (newList[j].goodsAttribute.toString()
                                    .toDouble() < newList[pos].goodsAttribute.toString().toDouble()
                            ) {
                                pos = j
                            }
                        }

                        mAstrologersList.add(newList.removeAt(pos))
                    }

                    notifyDataSetChanged()
                }
            }

            R.id.atvTotalOrdersHTL -> {
                if (previousSortId != R.id.atvTotalOrdersHTL) {
                    mAstrologersList.clear()

                    val newList = ArrayList<AstrologerListResponse>()
                    newList.addAll(mList)

                    for (i in newList.indices) {
                        var pos = 0
                        for (j in 1 until newList.size) {
                            if ((newList[j].goodsTotalRating ?: 0) > (newList[pos].goodsTotalRating
                                    ?: 0)
                            ) {
                                pos = j
                            }
                        }

                        mAstrologersList.add(newList.removeAt(pos))
                    }

                    notifyDataSetChanged()
                }
            }

            R.id.atvTotalOrdersLTH -> {
                if (previousSortId != R.id.atvTotalOrdersLTH) {
                    mAstrologersList.clear()

                    val newList = ArrayList<AstrologerListResponse>()
                    newList.addAll(mList)

                    for (i in newList.indices) {
                        var pos = 0
                        for (j in 1 until newList.size) {
                            if ((newList[j].goodsTotalRating ?: 0) < (newList[pos].goodsTotalRating
                                    ?: 0)
                            ) {
                                pos = j
                            }
                        }

                        mAstrologersList.add(newList.removeAt(pos))
                    }

                    notifyDataSetChanged()
                }
            }

            R.id.atvPriceHTL -> {
                if (previousSortId != R.id.atvPriceHTL) {
                    mAstrologersList.clear()

                    val newList = ArrayList<AstrologerListResponse>()
                    newList.addAll(mList)

                    for (i in newList.indices) {
                        var pos = 0
                        for (j in 1 until newList.size) {
                            if (newList[j].goodsPrice!! > newList[pos].goodsPrice!!) {
                                pos = j
                            }
                        }

                        mAstrologersList.add(newList.removeAt(pos))
                    }

                    notifyDataSetChanged()
                }
            }

            R.id.atvPriceLTH -> {
                if (previousSortId != R.id.atvPriceLTH) {
                    mAstrologersList.clear()

                    val newList = ArrayList<AstrologerListResponse>()
                    newList.addAll(mList)

                    for (i in newList.indices) {
                        var pos = 0
                        for (j in 1 until newList.size) {
                            if (newList[j].goodsPrice!! < newList[pos].goodsPrice!!) {
                                pos = j
                            }
                        }

                        mAstrologersList.add(newList.removeAt(pos))
                    }

                    notifyDataSetChanged()
                }
            }

            else -> {

            }
        }
    }


}