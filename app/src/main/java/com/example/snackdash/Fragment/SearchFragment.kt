package com.example.snackdash.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snackdash.adapter.MenuAdapter
import com.example.snackdash.databinding.FragmentSearchBinding
import com.example.snackdash.model.MenuItem
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MenuAdapter
    private val menuItems = mutableListOf<MenuItem>()      // all items
    private val filteredItems = mutableListOf<MenuItem>()  // filtered items

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = MenuAdapter(filteredItems, requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter

        setupSearchView()
        fetchMenuFromDatabase()

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenu(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenu(newText)
                return true
            }
        })
    }

    private fun fetchMenuFromDatabase() {
        database = FirebaseDatabase.getInstance().getReference("menu")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for (itemSnap in snapshot.children) {
                    val menuItem = itemSnap.getValue(MenuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }

                // Initially show all items
                filteredItems.clear()
                filteredItems.addAll(menuItems)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun filterMenu(query: String?) {
        filteredItems.clear()
        if (query.isNullOrBlank()) {
            filteredItems.addAll(menuItems)
        } else {
            menuItems.forEach { item ->
                if (item.foodName?.contains(query, ignoreCase = true) == true) {
                    filteredItems.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
