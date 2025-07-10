package com.example.snackdash.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snackdash.R
import com.example.snackdash.adapter.OrderAgainAdapter
import com.example.snackdash.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var orderAgainAdapter: OrderAgainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val orderAgainFoodName = arrayListOf("Pizza", "Burger", "Sandwich", "Roll")
        val orderAgainFoodPrice = arrayListOf("Rs400", "Rs300", "Rs200", "Rs150")
        val orderAgainFoodImage = arrayListOf(R.drawable.pizza, R.drawable.burger, R.drawable.sandwich, R.drawable.roll)
        orderAgainAdapter = OrderAgainAdapter(orderAgainFoodName, orderAgainFoodPrice, orderAgainFoodImage)
        binding.orderAgainRecyclerView.adapter = orderAgainAdapter
        binding.orderAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
    companion object {

    }
}