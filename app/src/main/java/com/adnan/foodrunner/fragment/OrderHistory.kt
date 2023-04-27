package com.adnan.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adnan.foodrunner.R
import com.adnan.foodrunner.adapter.orderAdapter
import com.adnan.foodrunner.model.orderHistory
import com.adnan.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class OrderHistory : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val view= inflater.inflate(R.layout.fragment_order_history, container, false)
        val rlLoading:RelativeLayout=view.findViewById(R.id.rlLoading)
        rlLoading.visibility=View.VISIBLE
        val EmptyOrder=view.findViewById(R.id.txtEmptyOrder) as TextView
        val progressBar:ProgressBar=view.findViewById(R.id.progressBar)
        val recycleView:RecyclerView=view.findViewById(R.id.recyclerOrderHistory)
        var orderAdapter: orderAdapter
        var orderList = arrayListOf<orderHistory>()
        val queue=Volley.newRequestQueue(activity as Context)
        var sharedPreferences=(activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        var user_id=sharedPreferences.getString("User_id","")
        val url="http://13.235.250.119/v2/orders/fetch_result/$user_id"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest=object:JsonObjectRequest(Method.GET,url,null, Response.Listener {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        rlLoading.visibility=View.GONE

                        val resArray = data.getJSONArray("data")
                        if(resArray.length()!=0){
                        for (i in 0 until resArray.length()) {
                                val resObject = resArray.getJSONObject(i)
                                val foodItems=resObject.getJSONArray("food_items")
                            val order=orderHistory(
                                    resObject.getString("order_id").toInt(),
                                    resObject.getString("restaurant_name"),
                                    resObject.getString("total_cost"),
                                    resObject.getString("order_placed_at"),
                                    foodItems
                                )
                                //Toast.makeText(context, "$order", Toast.LENGTH_LONG).show()

                                orderList.add(order)
                                val mLayoutManager = LinearLayoutManager(activity)
                                orderAdapter = orderAdapter(orderList, activity as Context)
                                recycleView.layoutManager = mLayoutManager
                                recycleView.adapter = orderAdapter
                                recycleView.setHasFixedSize(true)
                        }}
                        else{
                            rlLoading.visibility=View.VISIBLE
                            EmptyOrder.visibility=View.VISIBLE
                            progressBar.visibility=View.GONE

                        }

                        }
                    },Response.ErrorListener {
                Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
            })
            {


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"


                    headers["token"] = "230e80353ecc8f"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)


        }
        return view
    }
}