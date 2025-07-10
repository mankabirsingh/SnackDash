package com.example.snackdash.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snackdash.CongratsBottomSheet
import com.example.snackdash.PayoutActivity
import com.example.snackdash.R
import com.example.snackdash.adapter.CartAdapter
import com.example.snackdash.databinding.FragmentCartBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CartFragment : Fragment() {
    private lateinit var binding:FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)


        val cartFoodName = listOf("Pizza", "Burger", "Sandwich", "Roll")
        val cartItemPrice = listOf("Rs400", "Rs300", "Rs200", "Rs150")
        val cartImage = listOf(R.drawable.pizza, R.drawable.burger, R.drawable.sandwich, R.drawable.roll)
        val adapter = CartAdapter(ArrayList(cartFoodName), ArrayList(cartItemPrice), ArrayList(cartImage))
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter
        binding.nextButton.setOnClickListener {
            val intent = Intent(requireContext(), PayoutActivity::class.java)
            startActivity(intent)
        }
        
        return binding.root
    }

    companion object {

    }
}