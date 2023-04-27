package com.adnan.foodrunner.model

import org.json.JSONArray

data class orderHistory(
    val order_id: Int,
    val restaurant_name: String,
    val total_cost: String,
    val order_placed_at: String,
    val foodItems:JSONArray
)