package com.example.snackdash.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.snackdash.R
import com.example.snackdash.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        userRef = FirebaseDatabase.getInstance().reference.child("user").child(userId)

        // Load profile data
        loadUserProfile()

        // Save button click
        binding.saveButton.setOnClickListener {
            saveUserProfile()
        }

        return binding.root
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        binding.phoneEditText.setText(user?.phoneNumber ?: "")

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                binding.nameEditText.setText(snapshot.child("name").value?.toString() ?: "")
                binding.emailEditText.setText(snapshot.child("email").value?.toString() ?: "")
                binding.addressEditText.setText(snapshot.child("address").value?.toString() ?: "")
            }
        }
    }

    private fun saveUserProfile() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val address = binding.addressEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()

        if (name.isBlank() || email.isBlank() || address.isBlank()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val userData = mapOf(
            "name" to name,
            "email" to email,
            "address" to address,
            "phone" to phone
        )

        userRef.updateChildren(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to save profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
