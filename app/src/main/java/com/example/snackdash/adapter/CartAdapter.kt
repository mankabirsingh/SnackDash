package com.example.snackdash.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snackdash.databinding.CartItemsBinding
import com.example.snackdash.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItems>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId: String = auth.currentUser?.uid ?: ""
    private val cartRef: DatabaseReference = FirebaseDatabase.getInstance()
        .reference.child("users").child(userId).child("cartItems")

    // Track Firebase keys and quantities
    private val cartKeys: MutableList<String> = mutableListOf()
    private val itemQuantities: MutableList<Int> = mutableListOf()

    init {
        // Initialize cartKeys and quantities from current items
        cartItems.forEach { item ->
            itemQuantities.add(item.foodQuantity ?: 1)
        }
        fetchCartKeys()
    }

    private fun fetchCartKeys() {
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartKeys.clear()
                snapshot.children.forEach { dataSnapshot ->
                    cartKeys.add(dataSnapshot.key ?: "")
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch cart keys", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: CartItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = cartItems[position]
            val quantity = itemQuantities.getOrNull(position) ?: 1

            binding.apply {
                cartFoodName.text = item.foodName
                cartItemPrice.text = item.foodPrice
                cartItemQuantity.text = quantity.toString()
                Glide.with(context).load(Uri.parse(item.foodImage)).into(cartImage)

                // Increase quantity
                plus.setOnClickListener {
                    if (itemQuantities[position] < 10) {
                        itemQuantities[position]++
                        cartItemQuantity.text = itemQuantities[position].toString()
                        // Update Firebase
                        val key = cartKeys.getOrNull(position)
                        key?.let { cartRef.child(it).child("foodQuantity").setValue(itemQuantities[position]) }
                    }
                }

                // Decrease quantity
                minus.setOnClickListener {
                    val currentQuantity = itemQuantities.getOrNull(position) ?: 1

                    if (currentQuantity > 1) {
                        // Just decrease normally
                        itemQuantities[position] = currentQuantity - 1
                        cartItemQuantity.text = itemQuantities[position].toString()
                        // Update Firebase
                        val key = cartKeys.getOrNull(position)
                        key?.let { cartRef.child(it).child("foodQuantity").setValue(itemQuantities[position]) }

                    } else if (currentQuantity == 1) {
                        // If quantity is 1, deleting this item
                        deleteItem(position)
                    }
                }

                // Delete item
                delete.setOnClickListener {
                    deleteItem(position)
                }
            }
        }
    }

    private fun deleteItem(position: Int) {
        if (position !in cartKeys.indices) return

        val key = cartKeys[position]
        cartRef.child(key).removeValue().addOnSuccessListener {
            // Remove locally
            cartKeys.removeAt(position)
            cartItems.removeAt(position)
            itemQuantities.removeAt(position)

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to delete: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** Utility function to calculate total cost of items in cart */
    fun getTotalCost(): Int {
        var total = 0
        for (i in cartItems.indices) {
            val priceStr = cartItems[i].foodPrice?.replace("Rs", "")?.trim() ?: "0"
            val price = priceStr.toIntOrNull() ?: 0
            total += price * (itemQuantities.getOrNull(i) ?: 1)
        }
        return total
    }
}
