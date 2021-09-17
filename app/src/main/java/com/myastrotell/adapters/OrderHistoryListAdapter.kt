package com.myastrotell.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myastrotell.R
import com.myastrotell.data.OrderHistoryType
import com.myastrotell.interfaces.OrderHistoryItemClickListener
import com.myastrotell.pojo.response.ProductBillingDetails
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.gone
import com.myastrotell.utils.visible
import kotlinx.android.synthetic.main.list_item_mall_order.view.*
import kotlinx.android.synthetic.main.list_item_order_history.view.*
import kotlinx.android.synthetic.main.list_item_order_history.view.aivDelete
import kotlinx.android.synthetic.main.list_item_order_history.view.atvName
import kotlinx.android.synthetic.main.list_item_order_history.view.atvOrderId
import kotlinx.android.synthetic.main.list_item_order_history.view.atvStatus
import kotlinx.android.synthetic.main.list_item_order_history.view.sdvImage


class OrderHistoryListAdapter(
    private val mOrdersList: ArrayList<ProductBillingDetails>,
    private val type: String,
    private val mClickListener: OrderHistoryItemClickListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            0 -> {
                MallViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_mall_order, parent, false)
                )
            }

            else -> {
                ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_order_history, parent, false)
                )
            }
        }

    }


    override fun getItemCount() = mOrdersList.size


    override fun getItemViewType(position: Int): Int {
        return if (type.equals(OrderHistoryType.MALL.value, true)) {
            0
        } else {
            1
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val data = mOrdersList[position]

        when (getItemViewType(position)) {
            0 -> {
                // Astro-Mall Order
                val mallHolder = holder as MallViewHolder

                holder.sdvImage.setImageURI(data.imageURL)

                holder.atvOrderId.text = ("${context?.getString(R.string.order_id)} ${data.id}")

                holder.atvName.text = data.productName

                holder.atvPlacedOn.text =
                    AppUtils.timeStampToDate(data.redeemDate, AppUtils.DF_dd_MMM_yyyy)

                holder.atvLabelShipTo.gone()
                holder.atvShipTo.gone()

                holder.atvOrderTotal.text =
                    String.format(context!!.getString(R.string.money_format), data.redeemPoint)


                if (data.redeemFlag) {
                    holder.atvStatus.text = context?.getString(R.string.order_placed)
                    holder.atvStatus.setTextColor(
                        ContextCompat.getColor(context!!, R.color.colorGreen)
                    )

                } else {
                    holder.atvStatus.text = context?.getString(R.string.on_the_way)
                    holder.atvStatus.setTextColor(
                        ContextCompat.getColor(context!!, R.color.colorRed)
                    )
                }

            }

            else -> {
                // Chat/Call/Report Order

                val itemHolder = holder as ItemViewHolder

                holder.sdvImage.setImageURI(data.imageURL)

                holder.atvOrderId.text = ("${context?.getString(R.string.order_id)} ${data.id}")

                holder.atvName.text = data.productName

                val date = AppUtils.timeStampToDate(data.redeemDate, AppUtils.DF_dd_MMM_yyyy)
                holder.atvDateTime.text = ("${context?.getString(R.string.date)} $date")


                when (type) {

                    OrderHistoryType.CHAT.value -> {
                        if (data.redeemFlag) {
                            holder.atvStatus.text = context?.getString(R.string.completed)
                            holder.atvStatus.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorGreen)
                            )

                        } else {
                            holder.atvStatus.text = context?.getString(R.string.ongoing)
                            holder.atvStatus.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorRed)
                            )
                        }

                        holder.atvDetails.text =
                            ("Chat Rate: ${String.format(
                                context!!.getString(R.string.rate_format), data.redeemValue
                            )}" +
                                    "\nDuration: ${data.availableCount} min" +
                                    "\nDeduction: ${String.format(
                                        context!!.getString(R.string.money_format), data.redeemPoint
                                    )}")



                        holder.atvAction.text = context?.getString(R.string.chat)

                        if (data.isOnline) {
                            // Online

                            if (data.isBusy) {
                                // Busy
                                holder.atvAction.isEnabled = false

                                holder.atvAction.backgroundTintList =
                                    ContextCompat.getColorStateList(context!!, R.color.colorRed)

                                holder.atvRate.text = context?.getString(R.string.currently_busy)

                                holder.atvRate.setTextColor(
                                    ContextCompat.getColor(context!!, R.color.colorRed)
                                )

                            } else {
                                // available
                                holder.atvAction.isEnabled = true

                                holder.atvAction.backgroundTintList =
                                    ContextCompat.getColorStateList(context!!, R.color.colorGreen)

                                holder.atvRate.text =
                                    String.format(
                                        context!!.getString(R.string.rate_format), data.redeemValue
                                    )

                                holder.atvRate.setTextColor(
                                    ContextCompat.getColor(context!!, R.color.colorBlack)
                                )
                            }

                        } else {
                            // Offline
                            holder.atvAction.isEnabled = false

                            holder.atvAction.backgroundTintList =
                                ContextCompat.getColorStateList(context!!, R.color.colorGray)

                            holder.atvRate.text = context?.getString(R.string.currently_offline)

                            holder.atvRate.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorGrayText)
                            )
                        }

                        holder.atvViewReport.gone()

                    }

                    OrderHistoryType.CALL.value -> {
                        if (data.redeemFlag) {
                            holder.atvStatus.text = context?.getString(R.string.completed)
                            holder.atvStatus.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorGreen)
                            )

                        } else {
                            holder.atvStatus.text = context?.getString(R.string.ongoing)
                            holder.atvStatus.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorRed)
                            )
                        }

                        holder.atvDetails.text =
                            ("Call Rate: ${String.format(
                                context!!.getString(R.string.rate_format), data.redeemValue
                            )}" +
                                    "\nDuration: ${data.availableCount} min" +
                                    "\nDeduction: ${String.format(
                                        context!!.getString(R.string.money_format), data.redeemPoint
                                    )}")


                        holder.atvAction.text = context?.getString(R.string.call)

                        if (data.isOnline) {
                            // Online
                            if (data.isBusy) {
                                // Busy
                                holder.atvAction.isEnabled = false

                                holder.atvAction.backgroundTintList =
                                    ContextCompat.getColorStateList(context!!, R.color.colorRed)

                                holder.atvRate.text = context?.getString(R.string.currently_busy)

                                holder.atvRate.setTextColor(
                                    ContextCompat.getColor(context!!, R.color.colorRed)
                                )

                            } else {
                                // available
                                holder.atvAction.isEnabled = true

                                holder.atvAction.backgroundTintList =
                                    ContextCompat.getColorStateList(context!!, R.color.colorGreen)

                                holder.atvRate.text =
                                    String.format(
                                        context!!.getString(R.string.rate_format), data.redeemValue
                                    )

                                holder.atvRate.setTextColor(
                                    ContextCompat.getColor(context!!, R.color.colorBlack)
                                )
                            }

                        } else {
                            // offline
                            holder.atvAction.isEnabled = false

                            holder.atvAction.backgroundTintList =
                                ContextCompat.getColorStateList(context!!, R.color.colorGray)

                            holder.atvRate.text = context?.getString(R.string.currently_offline)

                            holder.atvRate.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorGrayText)
                            )
                        }

                        holder.atvViewReport.gone()

                    }

                    OrderHistoryType.REPORT.value -> {
                        if (data.redeemFlag) {
                            holder.atvStatus.text = context?.getString(R.string.completed)
                            holder.atvStatus.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorGreen)
                            )

                        } else {
                            holder.atvStatus.text = context?.getString(R.string.report_in_queue)
                            holder.atvStatus.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorRed)
                            )
                        }

                        holder.atvDetails.text = ("Deduction: ${String.format(
                            context!!.getString(R.string.money_format), data.redeemPoint
                        )}/report" +
                                "\nNo Review")

                        holder.atvAction.text = context?.getString(R.string.get_report)

                        // User can request for a report anytime
                        // regardless astrologer being online/offline

//                        if (data.isOnline) {

                            holder.atvAction.isEnabled = true

                            holder.atvAction.backgroundTintList =
                                ContextCompat.getColorStateList(context!!, R.color.colorGreen)

                            holder.atvRate.text = ("${String.format(
                                context!!.getString(R.string.money_format), data.redeemPoint
                            )}/report")

                            holder.atvRate.setTextColor(
                                ContextCompat.getColor(context!!, R.color.colorBlack)
                            )

//                        } else {
//                            holder.atvAction.isEnabled = false
//
//                            holder.atvAction.backgroundTintList =
//                                ContextCompat.getColorStateList(context!!, R.color.colorGray)
//
//                            holder.atvRate.text = context?.getString(R.string.currently_offline)
//
//                            holder.atvRate.setTextColor(
//                                ContextCompat.getColor(context!!, R.color.colorGrayText)
//                            )
//                        }

                        holder.atvViewReport.visible()

                    }

                }
            }

        }

    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvOrderId = itemView.atvOrderId!!
        val atvReportTitle = itemView.atvReportTitle!!
        val aivDelete = itemView.aivDelete!!
        val sdvImage = itemView.sdvImage!!
        val atvName = itemView.atvName!!
        val atvStatus = itemView.atvStatus!!
        val atvDateTime = itemView.atvDateTime!!
        val atvDetails = itemView.atvDetails!!
        val atvAction = itemView.atvAction!!
        val atvRate = itemView.atvRate!!
        val atvViewReport = itemView.atvViewReport!!

        init {
            atvAction.setOnClickListener {
                mClickListener?.onActionClicked(adapterPosition)
            }

            itemView.setOnClickListener {
                mClickListener?.onItemClicked(adapterPosition)
            }
        }

    }


    inner class MallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val atvOrderId = itemView.atvOrderId!!
        val aivDelete = itemView.aivDelete!!
        val sdvImage = itemView.sdvImage!!
        val atvName = itemView.atvName!!
        val atvStatus = itemView.atvStatus!!
        val atvPlacedOn = itemView.atvPlacedOn!!
        val atvLabelShipTo = itemView.atvLabelShipTo!!
        val atvShipTo = itemView.atvShipTo!!
        val atvOrderTotal = itemView.atvOrderTotal!!
        val atvViewDetails = itemView.atvViewDetails!!

        init {
            itemView.setOnClickListener {
                mClickListener?.onItemClicked(adapterPosition)
            }
        }

    }

}