package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.pojo.response.astrlogerprofile.ReviewModel
import com.myastrotell.utils.AppUtils
import kotlinx.android.synthetic.main.list_item_rating_reviews.view.*


class RatingReviewAdapter(
    private val mReviewsList: ArrayList<ReviewModel>
) : RecyclerView.Adapter<RatingReviewAdapter.ReviewsViewHolder>() {


    private var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        context = parent.context
        return ReviewsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_rating_reviews, parent, false)
        )
    }


    override fun getItemCount() = mReviewsList.size


    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {

        val data = mReviewsList[position]

        holder.atvNameChar.text = data.reviewerName ?: ""

        holder.atvName.text = data.reviewerName ?: ""

        holder.atvReview.text = data.review ?: ""

        data.rating?.let {
            holder.ratingBar.rating = data.rating?.toFloat()!!
        }

        holder.atvDate.text = AppUtils.parseDateTimeFormat(data.updateTimestamp, "yyyy-MM-dd'T'HH:mm:ss.sss+0000", "dd MMMM yyyy")

    }


    inner class ReviewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvNameChar = itemView.atvNameChar!!
        val atvName = itemView.atvName!!
        val atvReview = itemView.atvReview!!
        val ratingBar = itemView.ratingBar!!
        val atvDate = itemView.atvDate!!

    }

}