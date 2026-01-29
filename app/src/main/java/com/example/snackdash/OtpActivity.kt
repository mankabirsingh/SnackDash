package com.example.snackdash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snackdash.databinding.ActivityOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private val binding: ActivityOtpBinding by lazy {
        ActivityOtpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Retrieve verificationId from Intent (sent by StartActivity)
        storedVerificationId = intent.getStringExtra("verificationId")

        binding.otpContinueButton.setOnClickListener {
            val otpCode = binding.firstPinView.text.toString().trim()
            if (otpCode.isBlank()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (storedVerificationId != null) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otpCode)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Verification ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid
                    val phoneNumber = user.phoneNumber ?: "Unknown"

                    // Save user info in Realtime Database
                    val userRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                        .reference.child("user").child(userId)

                    val userData = mapOf(
                        "phone" to phoneNumber,
                        "role" to "customer" // optional field for distinction
                    )

                    userRef.setValue(userData).addOnCompleteListener { saveTask ->
                        if (saveTask.isSuccessful) {
                            Toast.makeText(this, "Welcome! Logged in successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Location::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

