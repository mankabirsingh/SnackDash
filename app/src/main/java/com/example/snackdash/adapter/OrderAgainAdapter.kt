package com.example.snackdash.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snackdash.databinding.BuyAgainItemBinding

class OrderAgainAdapter(private val orderAgainFoodName:ArrayList<String>, private val orderAgainFoodPrice:ArrayList<String>,
                        private val orderAgainFoodImage:ArrayList<Int>) : RecyclerView.Adapter<OrderAgainAdapter.OrderAgainViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderAgainViewHolder(binding)
    }

    override fun getItemCount(): Int = orderAgainFoodName.size

    override fun onBindViewHolder(holder: OrderAgainViewHolder, position: Int) {
        holder.bind(orderAgainFoodName[position], orderAgainFoodPrice[position], orderAgainFoodImage[position])
    }

    class OrderAgainViewHolder(private val binding: BuyAgainItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(foodName: String, foodPrice: String, foodImage: Int) {
            binding.orderAgainFoodName.text = foodName
            binding.OrderAgainFoodPrice.text = foodPrice
            binding.orderAgainFoodImage.setImageResource(foodImage)
        }

    }
}