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

class PastOrderAdapter(
    private val orders: MutableList<Order>,
    private val onOrderAgainClicked: (Order) -> Unit
) : RecyclerView.Adapter<PastOrderAdapter.PastOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.buy_again_item, parent, false)
        return PastOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastOrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class PastOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderId: TextView = itemView.findViewById(R.id.pastOrderId)
        private val itemsContainer: LinearLayout = itemView.findViewById(R.id.pastOrderItemsContainer)
        private val totalAmount: TextView = itemView.findViewById(R.id.pastOrderTotal)
        private val orderAgainButton: AppCompatButton = itemView.findViewById(R.id.orderAgainButton)

        fun bind(order: Order) {
            orderId.text = "Order ID: ${order.orderId}"
            totalAmount.text = "Total: Rs ${order.totalCost}"

            // Populate items dynamically
            itemsContainer.removeAllViews()
            order.items.forEach { item ->
                val tv = TextView(itemView.context)
                tv.text = "${item.foodName} x ${item.quantity}"
                tv.textSize = 14f
                itemsContainer.addView(tv)
            }

            orderAgainButton.setOnClickListener {
                onOrderAgainClicked(order)
            }
        }
    }
}


