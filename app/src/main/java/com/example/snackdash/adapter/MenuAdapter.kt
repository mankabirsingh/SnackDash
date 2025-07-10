package com.example.snackdash.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snackdash.databinding.MenuItemsBinding

class MenuAdapter(private val menuItemName:MutableList<String>, private val menuItemPrice:MutableList<String>, private val menuImages:MutableList<Int>): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder((binding))
    }
    
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }
    
    override fun getItemCount(): Int = menuItemName.size
    
    inner class MenuViewHolder(private val binding: MenuItemsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                menuFoodName.text = menuItemName[position]
                menuPrice.text = menuItemPrice[position]
                menuImage.setImageResource(menuImages[position])
            }
        }

    }
}