package com.example.snackdash.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snackdash.PayoutActivity
import com.example.snackdash.adapter.CartAdapter
import com.example.snackdash.databinding.FragmentCartBinding
import com.example.snackdash.model.CartItems
import com.example.snackdash.model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartAdapter: CartAdapter
    private val cartItemsList: MutableList<CartItems> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        fetchCartItems()

        binding.nextButton.setOnClickListener {
            val totalCost = cartAdapter.getTotalCost()
            if (totalCost == 0) {
                Toast.makeText(requireContext(), "Cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                // Convert CartItems to OrderItem for PayoutActivity
                val orderItems = cartItemsList.map { cartItem ->
                    OrderItem(
                        foodName = cartItem.foodName ?: "",
                        quantity = cartItem.foodQuantity ?: 1,
                        price = cartItem.foodPrice ?: "",
                        image = cartItem.foodImage ?: ""
                    )
                }

                val intent = Intent(requireContext(), PayoutActivity::class.java)
                intent.putExtra("totalCost", totalCost)
                intent.putExtra("items", ArrayList(orderItems))
                startActivity(intent)
            }
        }

        return binding.root
    }

    private fun fetchCartItems() {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.reference.child("users").child(userId).child("cartItems")

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItemsList.clear()
                for (child in snapshot.children) {
                    val cartItem = child.getValue(CartItems::class.java)
                    cartItem?.let { cartItemsList.add(it) }
                }
                setupRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        if (cartItemsList.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
        }

        cartAdapter = CartAdapter(requireContext(), cartItemsList)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = cartAdapter
    }
}


