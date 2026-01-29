package com.example.snackdash.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.snackdash.R
import com.example.snackdash.model.Order

class RecentOrderAdapter(
    private val orders: MutableList<Order>,
    private val onReceivedClicked: (Order) -> Unit
) : RecyclerView.Adapter<RecentOrderAdapter.RecentOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_order, parent, false)
        return RecentOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentOrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class RecentOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderId: TextView = itemView.findViewById(R.id.recentOrderId)
        private val itemsContainer: LinearLayout = itemView.findViewById(R.id.recentOrderItemsContainer)
        private val totalAmount: TextView = itemView.findViewById(R.id.recentOrderTotal)
        private val receivedButton: AppCompatButton = itemView.findViewById(R.id.receivedButton)
        private val statusText: TextView = itemView.findViewById(R.id.statusText)

        fun bind(order: Order) {
            orderId.text = "Order ID: ${order.orderId}"
            totalAmount.text = "Total: Rs ${order.totalCost}"

            // Populate items
            itemsContainer.removeAllViews()
            order.items.forEach { item ->
                val tv = TextView(itemView.context)
                tv.text = "${item.foodName} × ${item.quantity}"
                tv.textSize = 14f
                itemsContainer.addView(tv)
            }

            // Status mapping
            val displayStatus = when (order.status) {
                "pending" -> "Pending"
                "preparing" -> "Preparing"
                "out_for_delivery" -> "Out for Delivery"
                else -> "Completed"
            }
            statusText.text = displayStatus

            // Enable button only if dispatched
            if (order.status == "out_for_delivery") {
                receivedButton.isEnabled = true
                receivedButton.setBackgroundColor(itemView.context.getColor(R.color.red_new))
            } else {
                receivedButton.isEnabled = false
                receivedButton.setBackgroundColor(itemView.context.getColor(R.color.gray))
            }

            receivedButton.setOnClickListener {
                if (receivedButton.isEnabled) {
                    onReceivedClicked(order)
                }
            }
        }
    }
}
