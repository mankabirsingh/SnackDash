package com.example.snackdash.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snackdash.databinding.FrequentOrdersBinding

class FrequentAdapter (private val items:List<String>, private val price:List<String>, private val image:List<Int>): RecyclerView.Adapter<FrequentAdapter.FrequentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrequentViewHolder {
        return FrequentViewHolder(FrequentOrdersBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FrequentViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val prices = price[position]
        holder.bind(item, images, prices)
    }

    class FrequentViewHolder (private val binding:FrequentOrdersBinding) : RecyclerView.ViewHolder(binding.root){
        private val imageView = binding.cartImage
        fun bind(item: String, images: Int, prices: String) {
            binding.FoodName.text = item
            binding.Price.text = prices
            imageView.setImageResource(images)
        }
    }
}