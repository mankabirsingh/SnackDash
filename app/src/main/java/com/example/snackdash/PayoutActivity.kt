package com.example.snackdash

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snackdash.databinding.ActivityPayoutBinding
import com.example.snackdash.model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.R
import java.util.UUID

class PayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayoutBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var items: List<OrderItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("user") // user profile

        // Back button
        binding.imageButton.setOnClickListener { finish() }

        // Total cost
        val totalCost = intent.getIntExtra("totalCost", 0)
        binding.totalAmount.text = "Rs$totalCost"

        // Deserialize items
        val itemsSerializable = intent.getSerializableExtra("items")
        if (itemsSerializable is ArrayList<*>) {
            @Suppress("UNCHECKED_CAST")
            items = itemsSerializable.filterIsInstance<OrderItem>()
        }

        populateOrderItems()
        fetchUserProfile()

        binding.placeOrderButton.setOnClickListener {
            placeOrder(totalCost)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.example.snackdash.R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun populateOrderItems() {
        binding.orderItemsContainer.removeAllViews()
        if (items.isEmpty()) return
        for (item in items) {
            val tv = TextView(this)
            tv.text = "${item.foodName} × ${item.quantity}"
            tv.textSize = 16f
            tv.setPadding(8, 8, 8, 8)
            binding.orderItemsContainer.addView(tv)
        }
    }

    private fun fetchUserProfile() {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        binding.editTextPhone.setText(currentUser.phoneNumber ?: "")

        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.editTextName.setText(snapshot.child("name").getValue(String::class.java) ?: "")
                    binding.editTextAddress.setText(snapshot.child("address").getValue(String::class.java) ?: "")
                    binding.editTextEmail.setText(snapshot.child("email").getValue(String::class.java) ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PayoutActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun placeOrder(totalCost: Int) {
        val name = binding.editTextName.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val phone = binding.editTextPhone.text.toString().trim()

        if (name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        val orderId = UUID.randomUUID().toString()
        val orderRef = FirebaseDatabase.getInstance().getReference("orders")
            .child(uid)
            .child(orderId)

        val orderData = mapOf(
            "orderId" to orderId,
            "name" to name,
            "address" to address,
            "email" to email,
            "phone" to phone,
            "totalCost" to totalCost,
            "timestamp" to System.currentTimeMillis(),
            "status" to "pending", // ✅ changed from "recent"
            "items" to items.map {
                mapOf(
                    "foodName" to it.foodName,
                    "quantity" to it.quantity,
                    "price" to it.price,
                    "image" to it.image
                )
            }
        )

        orderRef.setValue(orderData)
            .addOnSuccessListener {
                showOrderConfirmation()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showOrderConfirmation() {
        val bottomSheet = CongratsBottomSheet()
        bottomSheet.show(supportFragmentManager, "CongratsBottomSheet")
    }
}
