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
import com.example.snackdash.adapter.RecentOrderAdapter
import com.example.snackdash.adapter.PastOrderAdapter
import com.example.snackdash.databinding.FragmentHistoryBinding
import com.example.snackdash.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var recentAdapter: RecentOrderAdapter
    private lateinit var pastAdapter: PastOrderAdapter

    private val recentOrders = mutableListOf<Order>()
    private val pastOrders = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("orders").child(uid)

        recentAdapter = RecentOrderAdapter(recentOrders) { order ->
            markOrderAsReceived(order)
        }

        pastAdapter = PastOrderAdapter(pastOrders) { order ->
            orderAgain(order)
        }

        binding.recentlyOrderedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentAdapter
        }

        binding.pastOrdersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pastAdapter
        }

        fetchOrders()
    }

    private fun fetchOrders() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recentOrders.clear()
                pastOrders.clear()

                for (orderSnap in snapshot.children) {
                    val orderId = orderSnap.key ?: continue
                    val timestamp = orderSnap.child("timestamp").getValue(Long::class.java) ?: 0L
                    val totalCost = orderSnap.child("totalCost").getValue(Int::class.java) ?: 0
                    val status = orderSnap.child("status").getValue(String::class.java) ?: "pending"
                    val name = orderSnap.child("name").getValue(String::class.java) ?: ""
                    val address = orderSnap.child("address").getValue(String::class.java) ?: ""
                    val email = orderSnap.child("email").getValue(String::class.java) ?: ""
                    val phone = orderSnap.child("phone").getValue(String::class.java) ?: ""

                    val items = mutableListOf<com.example.snackdash.model.OrderItem>()
                    val itemsSnap = orderSnap.child("items")
                    for (itemSnap in itemsSnap.children) {
                        val foodName = itemSnap.child("foodName").getValue(String::class.java) ?: ""
                        val quantity = itemSnap.child("quantity").getValue(Int::class.java) ?: 0
                        val price = itemSnap.child("price").getValue(String::class.java) ?: ""
                        val image = itemSnap.child("image").getValue(String::class.java) ?: ""
                        items.add(com.example.snackdash.model.OrderItem(foodName, quantity, price, image))
                    }

                    val order = Order(orderId, timestamp, items, totalCost, status, name, address, email, phone)

                    if (status == "pending" || status == "preparing" || status == "out_for_delivery") {
                        recentOrders.add(order)
                    } else {
                        pastOrders.add(order)
                    }
                }

                recentAdapter.notifyDataSetChanged()
                pastAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun markOrderAsReceived(order: Order) {
        val uid = auth.currentUser?.uid ?: return
        if (order.status != "out_for_delivery") {
            Toast.makeText(context, "Order is not yet out for delivery", Toast.LENGTH_SHORT).show()
            return
        }

        database.child(order.orderId).child("status").setValue("completed")
            .addOnSuccessListener {
                Toast.makeText(context, "Order received", Toast.LENGTH_SHORT).show()
                fetchOrders()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update order", Toast.LENGTH_SHORT).show()
            }
    }

    private fun orderAgain(order: Order) {
        val intent = Intent(requireContext(), PayoutActivity::class.java)
        intent.putExtra("totalCost", order.totalCost)
        intent.putExtra("items", ArrayList(order.items))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
