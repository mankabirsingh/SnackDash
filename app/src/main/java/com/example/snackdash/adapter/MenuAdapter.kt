package com.example.snackdash.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snackdash.databinding.MenuItemsBinding
import com.example.snackdash.model.CartItems
import com.example.snackdash.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    // Initialize FirebaseAuth here once
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuPrice.text = menuItem.price
                val uri = Uri.parse(menuItem.image)
                Glide.with(requireContext).load(uri).into(menuImage)

                menuAddToCart.setOnClickListener {
                    addItemToCart(menuItem)
                }
            }
        }

        private fun addItemToCart(menuItem: MenuItem) {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(requireContext, "Please sign in first", Toast.LENGTH_SHORT).show()
                return
            }

            val database = FirebaseDatabase.getInstance().reference
            val cartRef = database.child("users").child(userId).child("cartItems")

            // Use food name as a unique key
            val cartItemKey = menuItem.foodName ?: return

            // Check if item already exists in cart
            cartRef.child(cartItemKey).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Item already in cart
                    Toast.makeText(requireContext, "Item already in cart", Toast.LENGTH_SHORT).show()
                } else {
                    // Add new item
                    val cartItem = CartItems(
                        foodName = menuItem.foodName,
                        foodPrice = menuItem.price,
                        foodImage = menuItem.image,
                        foodQuantity = 1
                    )

                    cartRef.child(cartItemKey).setValue(cartItem)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext, "Item added to cart successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext, "Item not added: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext, "Error checking cart: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
