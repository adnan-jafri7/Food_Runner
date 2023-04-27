package com.adnan.foodrunner.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.adnan.foodrunner.R
import com.adnan.foodrunner.adapter.CartAdapter
import com.adnan.foodrunner.adapter.RestaurantMenuAdapter
import com.adnan.foodrunner.database.OrderDatabase
import com.adnan.foodrunner.database.OrderEntity
import com.adnan.foodrunner.model.RestaurantDetails
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var recyclerOrderDetail: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartAdapter
    lateinit var btnPlaceOrder: Button
    var orderList = ArrayList<RestaurantDetails>()
    lateinit var txtRestaurantName: TextView
    lateinit var sharedPrefs: SharedPreferences
    var resId: Int? = 0
    var resName: String? = ""
    var userId: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        progressBar = findViewById(R.id.progressBar)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        recyclerOrderDetail = findViewById(R.id.recyclerOrderDetail)
        progressLayout = findViewById(R.id.progressLayout)
        txtRestaurantName = findViewById(R.id.txtRestaurantName)
        sharedPrefs = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        toolbar = findViewById(R.id.toolbar)

        userId = sharedPrefs.getString("user_id", "")
        val bundle=intent.getBundleExtra("data")
        resId=bundle?.getInt("resId",0)
        resName=bundle?.getString("resName","")
        layoutManager = LinearLayoutManager(this)
        txtRestaurantName.text = resName
        setupToolbar()
        val dbOrderList = RetrieveOrders(applicationContext).execute().get()
        for (element in dbOrderList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<RestaurantDetails>::class.java).asList()
            )
        }
        progressLayout.visibility = View.GONE
        recyclerAdapter = CartAdapter(this, orderList)
        recyclerOrderDetail.adapter = recyclerAdapter
        recyclerOrderDetail.layoutManager = layoutManager

        var sum = 0
        for (i in 0 until orderList.size) {
            //sum += orderList[i].foodCost.toInt()
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total
        val sums=sum.toString()
        btnPlaceOrder.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", resId)
            jsonParams.put("total_cost", sums)
            val foodArray = JSONArray()
            for (i in 0 until orderList.size) {
                val foodId1 = JSONObject()
                foodId1.put("food_item_id", orderList[i].foodId)
                foodArray.put(i, foodId1)
            }
            jsonParams.put("food", foodArray)
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    try {
                        if (success) {
                            ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            RestaurantMenuAdapter.isCartEmpty = true
                            val dialog = Dialog(
                                this@CartActivity,
                                android.R.style.Theme_Black_NoTitleBar_Fullscreen
                            )
                            dialog.setContentView(R.layout.order_placed_dialog)
                            dialog.show()
                            dialog.setCancelable(false)
                            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                            btnOk.setOnClickListener {
                                dialog.dismiss()
                                startActivity(Intent(this@CartActivity, HomeActivity::class.java))
                                ActivityCompat.finishAffinity(this@CartActivity)
                            }
                        } else {

                            Toast.makeText(
                                this@CartActivity,
                                "$it",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {

                        Toast.makeText(
                            this@CartActivity,
                            "$e",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }, Response.ErrorListener {

                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "230e80353ecc8f"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }
    }

    fun setupToolbar() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class ClearDBAsync(val context: Context, val resID: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
            db.orderDao().deleteOrders(resID)
            db.close()
            return true
        }

    }

    class RetrieveOrders(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").fallbackToDestructiveMigration().build()
            return db.orderDao().getAllOrders()
        }

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            ClearDBAsync(applicationContext, resId.toString()).execute().get()
            RestaurantMenuAdapter.isCartEmpty = true
            startActivity(Intent(this, RestaurantMenu::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val clearCart=ClearDBAsync(applicationContext,resId.toString()).execute().get()
        RestaurantMenuAdapter.isCartEmpty=true
        onBackPressed()
        return true
    }
}