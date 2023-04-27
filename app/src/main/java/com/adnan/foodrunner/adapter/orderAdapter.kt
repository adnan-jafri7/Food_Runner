package com.adnan.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adnan.foodrunner.R
import com.adnan.foodrunner.fragment.OrderHistory
import com.adnan.foodrunner.model.RestaurantDetails
import com.adnan.foodrunner.model.Restaurants
import com.adnan.foodrunner.model.orderHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class orderAdapter(private var orders: ArrayList<orderHistory>, val context: Context) :
    RecyclerView.Adapter<orderAdapter.orderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): orderAdapter.orderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_row_order_history, parent, false)
        return orderViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: orderAdapter.orderViewHolder, p: Int) {
        val resObject = orders.get(p)
        holder.restaurantName.text=resObject.restaurant_name
        holder.Date.text=formatDate(resObject.order_placed_at)
        setUpRecycler(holder.recyclerOrderHistory,resObject)

    }

    class orderViewHolder(view:View):RecyclerView.ViewHolder(view) {
        var restaurantName:TextView=view.findViewById(R.id.txtRestaurantName)
        val Date:TextView=view.findViewById(R.id.txtDate)
        var recyclerOrderHistory:RecyclerView=view.findViewById(R.id.recyclerOrderHistory)
    }

    fun setUpRecycler(recyclerOrderHistory: RecyclerView,orders:orderHistory){
        val foodItemsList=ArrayList<RestaurantDetails>()
        for(i in 0 until orders.foodItems.length()){
            val foodJson=orders.foodItems.getJSONObject(i)
            foodItemsList.add(
                RestaurantDetails(
                    foodId =  foodJson.getString("food_item_id"),
                    foodName =  foodJson.getString("name"),
                    foodCost =  foodJson.getString("cost")
                )
            )
        }
        val cartAdapter=CartAdapter(context,foodItemsList)
        val layoutManager= LinearLayoutManager(context)
        recyclerOrderHistory.layoutManager=layoutManager
        recyclerOrderHistory.adapter=cartAdapter

    }
    fun formatDate(dateString:String):String? {
        val inputFormatter=SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date:Date=inputFormatter.parse(dateString) as Date
        val outputFormatter= SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }

}