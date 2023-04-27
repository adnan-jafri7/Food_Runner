package com.adnan.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adnan.foodrunner.R
import com.adnan.foodrunner.activity.RestaurantMenu
import com.adnan.foodrunner.model.MenuList
import com.adnan.foodrunner.model.Restaurants

class RestaurantMenuAdapter(var restaurantMenu: ArrayList<MenuList>, val context: Context, val listener: onItemClickListener):RecyclerView.Adapter<RestaurantMenuAdapter.RestaurantMenuViewHolder>() {
    companion object{
        var isCartEmpty=true
    }
    class RestaurantMenuViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtSr: TextView =view.findViewById(R.id.txtSr) as TextView
        val txtItem:TextView=view.findViewById(R.id.txtItem) as TextView
        val txtItemPrice:TextView=view.findViewById(R.id.txtItemPrice) as TextView
        val btnAddToCart: Button =view.findViewById(R.id.btnAddToCart) as Button
        val btnRemoveFromCart:Button=view.findViewById(R.id.btnRemoveFromCart) as Button

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.activity_restaurant_menu_adapter,parent,false)
        return RestaurantMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantMenu.size
    }

    interface onItemClickListener{
        fun onAddItemClick(orderDetail: MenuList)
        fun onRemoveItemClick(orderDetail:MenuList)
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, p: Int) {
        val restaurant = restaurantMenu[p]
        var s=p
        holder.txtSr.text =(++s).toString()
        holder.txtItem.text = restaurant.name
        holder.txtItemPrice.text = "Rs.${restaurant.cost_for_one}"
        holder.btnAddToCart.setOnClickListener {
            holder.btnAddToCart.visibility=View.GONE
            holder.btnRemoveFromCart.visibility=View.VISIBLE
            listener.onAddItemClick(restaurant)

        }

        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility=View.GONE
            holder.btnAddToCart.visibility=View.VISIBLE
            listener.onRemoveItemClick(restaurant)
        }
    }
}