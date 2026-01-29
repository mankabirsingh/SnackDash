package com.example.snackdash.model

import java.io.Serializable

data class OrderItem(
    val foodName: String = "",
    val quantity: Int = 0,
    val price: String = "",
    val image: String = ""
) : Serializable


data class Order(
    val orderId: String = "",
    val timestamp: Long = 0L,
    val items: List<OrderItem> = listOf(),
    val totalCost: Int = 0,
    val status: String = "recent", // "recent" or "past"
    val name: String = "",
    val address: String = "",
    val email: String = "",
    val phone: String = ""
)