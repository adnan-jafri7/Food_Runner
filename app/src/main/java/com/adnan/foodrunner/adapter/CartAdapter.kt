package com.adnan.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.adnan.foodrunner.R
import com.adnan.foodrunner.database.OrderEntity
import com.adnan.foodrunner.model.RestaurantDetails


class CartAdapter(val context: Context,val orderList:ArrayList<RestaurantDetails>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_cart_adapter, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObject=orderList[position]
        holder.txtFoodItem.text=cartObject.foodName
        holder.txtFoodItemPrice.text="Rs.${cartObject.foodCost}"


    }
    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtFoodItem: TextView =view.findViewById(R.id.txtFoodItem)
        val txtFoodItemPrice:TextView=view.findViewById(R.id.txtFoodItemPrice)
    }
}