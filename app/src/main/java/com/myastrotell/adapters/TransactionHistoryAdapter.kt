package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.data.TransactionCategory
import com.myastrotell.pojo.response.WalletTransactionResponse
import com.myastrotell.utils.AppUtils
import kotlinx.android.synthetic.main.list_item_transaction_history.view.*
import kotlin.math.abs


class TransactionHistoryAdapter(
    private val mTransactionList: ArrayList<WalletTransactionResponse>
) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewHolder>() {


    private var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        context = parent.context
        return TransactionViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.list_item_transaction_history, parent, false)
        )
    }


    override fun getItemCount() = mTransactionList.size


    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {

        mTransactionList[position].let { data ->

            var description: String

            when (data.category) {
                TransactionCategory.CHAT.value -> {
                    description = ("Deduction against chat with astrologer")
                }

                TransactionCategory.CALL.value -> {
                    description = ("Deduction against call with astrologer")
                }

                TransactionCategory.REPORT.value -> {
                    description = ("Deduction against report from astrologer")
                }

                TransactionCategory.ADD_MONEY.value -> {
                    description = ("Added money to wallet")
                }

                TransactionCategory.PROMO_MONEY.value -> {
                    description = ("Cash back received")
                }
                TransactionCategory.REFUND.value -> {
                    description = ("Refund")
                }


                else -> {
                    description = ("Deduction against order for ${data.mode}")
                }
            }

//            data.type?.let {
//                if(it=="RF")
//            }

            holder.atvDetails.text = description

            val amountPrefix = if (data.type.equals("DR", true)) "-" else "+"
            holder.atvAmount.text =
                ("$amountPrefix ${context?.getString(R.string.money_format, abs(data.money ?: 0.0))}")


            holder.atvDateTime.text = AppUtils.parseDateTimeFormat(
                data.dateTime,
                "yyyy-MM-dd HH:mm:ss.s",
                AppUtils.DF_dd_MMM_yy_hh_mm_aa
            )

        }
    }


    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvDetails = itemView.atvDetails!!
        val atvDateTime = itemView.atvDateTime!!
        val atvAmount = itemView.atvAmount!!

    }

}