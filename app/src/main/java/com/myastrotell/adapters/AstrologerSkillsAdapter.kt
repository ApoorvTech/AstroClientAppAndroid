package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import kotlinx.android.synthetic.main.list_item_astrologer_skill.view.*


class AstrologerSkillsAdapter(
    private val mSkillsList:List<String>
) : RecyclerView.Adapter<AstrologerSkillsAdapter.SkillsViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        context = parent.context
        return SkillsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_astrologer_skill, parent, false)
        )
    }

    override fun getItemCount() = mSkillsList.size

    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        holder.atvSkill.text=mSkillsList[position]
    }

    inner class SkillsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvSkill = itemView.atvSkill!!

    }

}