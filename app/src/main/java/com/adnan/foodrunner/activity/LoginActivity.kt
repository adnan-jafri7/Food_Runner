package com.adnan.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.adnan.foodrunner.R
import com.adnan.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var sharedPreferences=getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        var IsLoggedIn=sharedPreferences.getBoolean("IsLoggedIn",false)

        if(IsLoggedIn){
            intent=Intent(this@LoginActivity,
                HomeActivity::class.java)
            startActivity(intent)
            finish()

        }
        var txtForgotPassword:TextView
        var txtSignUp:TextView
        var etMobile:EditText
        var etPassword:EditText
        var btnLogin: Button
        var progressBar:ProgressBar

        txtForgotPassword=findViewById(R.id.txtForgotPassword)
        txtSignUp=findViewById(R.id.txtSignUp)
        etMobile=findViewById(R.id.etMobile)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogin)
        progressBar=findViewById(R.id.ProgressBar)
        if (ConnectionManager().checkConnectivity(this@LoginActivity)){
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"

        txtForgotPassword.setOnClickListener{
            intent=Intent(this@LoginActivity,
                ForgotPassword::class.java)
            startActivity(intent)
            finish()
        }
        txtSignUp.setOnClickListener {
           var intent2=Intent(this@LoginActivity, Register::class.java)
            startActivity(intent2)
            finish()

        }
        btnLogin.setOnClickListener{


            var Mobile=etMobile.text.toString()
            var password=etPassword.text.toString()
            val intent=Intent(this@LoginActivity,
                HomeActivity::class.java)
            if((Mobile.isEmpty()) || (password.isEmpty())){
                Toast.makeText(this@LoginActivity,"Invalid Credentials",Toast.LENGTH_LONG).show()
            }
            else if(Mobile.length<10){
                Toast.makeText(this@LoginActivity,"Invalid Mobile Number",Toast.LENGTH_LONG).show()
            }

            else{
                btnLogin.visibility=View.INVISIBLE
                progressBar.visibility= View.VISIBLE
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", Mobile)
                jsonParams.put("password", password)
                val jsonRequest=object:JsonObjectRequest(
                    Method.POST,url,jsonParams,
                    Response.Listener{

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {
                                progressBar.visibility=View.GONE
                                btnLogin.visibility=View.VISIBLE

                                val dataFetched = data.getJSONObject("data")

                                sharedPreferences.edit()
                                    .putString("User_id", dataFetched.getString("user_id")).apply()
                                sharedPreferences.edit()
                                    .putString("Mobile", dataFetched.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("Name", dataFetched.getString("name")).apply()
                                sharedPreferences.edit()
                                    .putString("Address", dataFetched.getString("address")).apply()
                                sharedPreferences.edit()
                                    .putString("Email", dataFetched.getString("email")).apply()
                                sharedPreferences.edit().putBoolean("IsLoggedIn", true).apply()
                                startActivity(intent)
                                finish()
                            }
                            else{
                                progressBar.visibility=View.GONE
                                btnLogin.visibility=View.VISIBLE
                                val errorMessage:String?=data.getString("errorMessage")
                                Toast.makeText(this@LoginActivity,"$errorMessage",Toast.LENGTH_SHORT).show()
                            }

                    },
                            Response.ErrorListener{
                                progressBar.visibility=View.GONE
                                btnLogin.visibility=View.VISIBLE
                                Toast.makeText(this@LoginActivity,"Volley Error Occurred",Toast.LENGTH_SHORT).show()
                            }
                ){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "230e80353ecc8f"
                        return headers
                    }
                }
                queue.add(jsonRequest)}

                }

            }
        else{
            val dialog = AlertDialog.Builder(this@LoginActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(this@LoginActivity)
            }
            dialog.create()
            dialog.show()
        }
        }

    }