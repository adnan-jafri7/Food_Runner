package com.adnan.foodrunner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.adnan.foodrunner.R
import com.adnan.foodrunner.adapter.RestaurantMenuAdapter
import com.adnan.foodrunner.database.OrderEntity
import com.adnan.foodrunner.database.OrderDatabase
import com.adnan.foodrunner.model.MenuList
import com.adnan.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.google.gson.Gson

class RestaurantMenu : AppCompatActivity() {
    lateinit var recyclerRestaurants: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantMenuAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var RestaurantMenuList = arrayListOf<MenuList>()
    var orderList= arrayListOf<MenuList>()
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var btnProceedToCart:Button
        var resId:String?="100"
        var resName:String?="name"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        recyclerRestaurants = findViewById(R.id.recyclerRestaurants)
        progressLayout=findViewById(R.id.rlLoading)
        progressBar=findViewById(R.id.progressBar)
        progressLayout.visibility= View.VISIBLE
        btnProceedToCart=findViewById(R.id.btnProceedToCart)
        layoutManager = LinearLayoutManager(this)
        resId= intent.getIntExtra("resid",0).toString()
        resName=intent.getStringExtra("resName")
        btnProceedToCart.setOnClickListener {
            proceedToCart()
        }
        val queue= Volley.newRequestQueue(this)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/$resId"
        if (ConnectionManager().checkConnectivity(this@RestaurantMenu)) {
            val jsonRequest=object: JsonObjectRequest(
                Method.GET,url,null,
                Response.Listener{
                    progressLayout.visibility=View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                        if(success){
                            val data=data.getJSONArray("data")
                            for(i in 0 until data.length()){
                                val JsonObject=data.getJSONObject(i)
                                val RestaurantObject= MenuList(
                                    JsonObject.getString("id"),
                                    JsonObject.getString("name"),
                                    JsonObject.getString("cost_for_one"),
                                    JsonObject.getString("restaurant_id")
                                )
                                RestaurantMenuList.add(RestaurantObject)
                                recyclerAdapter = RestaurantMenuAdapter(RestaurantMenuList,this@RestaurantMenu,object: RestaurantMenuAdapter.onItemClickListener{
                                    override fun onAddItemClick(orderDetail: MenuList) {
                                        orderList.add(orderDetail)
                                        if(orderList.size>0){
                                            btnProceedToCart.visibility=View.VISIBLE
                                            RestaurantMenuAdapter.isCartEmpty=false
                                        }
                                    }

                                    override fun onRemoveItemClick(orderDetail: MenuList) {
                                        orderList.remove(orderDetail)
                                        if(orderList.isEmpty()){
                                            btnProceedToCart.visibility=View.GONE
                                            RestaurantMenuAdapter.isCartEmpty=true
                                        }
                                    }
                                })
                                recyclerRestaurants.adapter = recyclerAdapter
                                recyclerRestaurants.layoutManager = layoutManager

        }
        }
                    else{
                            val errorMessage=data.getString("errorMessage")
                            Toast.makeText(this@RestaurantMenu,"$errorMessage",Toast.LENGTH_SHORT).show()
                        }
                    },Response.ErrorListener {
                    Toast.makeText(this@RestaurantMenu,"Some error occurred",Toast.LENGTH_SHORT).show()
                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "230e80353ecc8f"
                    return headers
                }
            }
            queue.add(jsonRequest)
        }

    }
    private fun proceedToCart(){
        val gson=Gson()
        val foodItems=gson.toJson(orderList)
        val async=ItemsOfCart(this@RestaurantMenu,resId.toString(),foodItems,1).execute()
        val result=async.get()
        if(result){
            val data=Bundle()
            data.putInt("resId",resId as Int)
            data.putString("resName",resName)
            val intent=Intent(this@RestaurantMenu,CartActivity::class.java)
            intent.putExtra("data",data)
            startActivity(intent)}
        else {
            Toast.makeText(this@RestaurantMenu, "Some error occurred", Toast.LENGTH_SHORT).show()
        }

        }
    class ItemsOfCart(context: Context, private val restaurantId:String, private val foodItems:String, val mode:Int):
        AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context, OrderDatabase::class.java,"order-db").fallbackToDestructiveMigration().build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    db.orderDao().insertOrder(OrderEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }

                2->{
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }

    }



    }

