package com.adnan.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.adnan.foodrunner.R
import com.adnan.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject


class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Register Yourself"
        val etName: EditText
        val etMobile: EditText
        val etEmail: EditText
        val etAddress: EditText
        val etPassword: EditText
        val etConfirmPassword: EditText
        val sharedPreferences: SharedPreferences
        val progressBar:ProgressBar=findViewById(R.id.ProgressBar)


        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        etName = findViewById(R.id.etName)
        etMobile = findViewById(R.id.etMobile)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        if (ConnectionManager().checkConnectivity(this@Register)){
        val queue = Volley.newRequestQueue(this@Register)
        val url = "http://13.235.250.119/v2/register/fetch_result"
        btnRegister.setOnClickListener {
            var Name = etName.text.toString()
            var Mobile = etMobile.text.toString()
            var Email = etEmail.text.toString()
            var Address = etAddress.text.toString()
            var Password = etPassword.text.toString()
            var ConfirmPassword = etConfirmPassword.text.toString()

            if ((Name.isEmpty()) || (Mobile.isEmpty()) || (Email.isEmpty()) || (Address.isEmpty()) || (Password.isEmpty()) || (ConfirmPassword.isEmpty())) {
                Toast.makeText(this@Register, "All fields are required", Toast.LENGTH_SHORT).show()

            } else if (Name.length < 3) {
                Toast.makeText(
                    this@Register,
                    "Name must contain minimum 3 Characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Mobile.length != 10) {
                Toast.makeText(this@Register, "Invalid Mobile Number", Toast.LENGTH_SHORT).show()
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches())) {
                Toast.makeText(this@Register, "Invalid Email", Toast.LENGTH_SHORT).show()
            } else if (Password.length < 6 || Password.length > 15) {
                Toast.makeText(
                    this@Register,
                    "Password must be of 6-15 Characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Password != ConfirmPassword) {
                Toast.makeText(
                    this@Register,
                    "Password and Confirm Password does not match",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                btnRegister.visibility= View.INVISIBLE
                progressBar.visibility=View.VISIBLE
                val jsonParams = JSONObject()
                jsonParams.put("name", Name)
                jsonParams.put("mobile_number", Mobile)
                jsonParams.put("password", Password)
                jsonParams.put("address", Address)
                jsonParams.put("email", Email)
                val jsonRequest = object : JsonObjectRequest(
                    Method.POST, url, jsonParams,
                    Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                btnRegister.visibility= View.VISIBLE
                                progressBar.visibility=View.INVISIBLE

                                val dataFetched=data.getJSONObject("data")

                                sharedPreferences.edit().putString("User_id", dataFetched.getString("user_id")).apply()
                                sharedPreferences.edit().putString("Mobile", dataFetched.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("Name", dataFetched.getString("name")).apply()
                                sharedPreferences.edit().putString("Address", dataFetched.getString("address")).apply()
                                sharedPreferences.edit().putString("Email", dataFetched.getString("email")).apply()
                                sharedPreferences.edit().putBoolean("IsLoggedIn",true).apply()
                                Toast.makeText(
                                    this@Register,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                intent = Intent(this@Register, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                btnRegister.visibility= View.VISIBLE
                                progressBar.visibility=View.INVISIBLE
                                val errorMessage=data.getString("errorMessage")
                                Toast.makeText(this@Register,"$errorMessage",Toast.LENGTH_SHORT).show()
                            }

                        } catch (e1: JSONException) {
                            btnRegister.visibility= View.VISIBLE
                            progressBar.visibility=View.INVISIBLE
                            Toast.makeText(this@Register, "Some Error Occurred", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    Response.ErrorListener {
                        btnRegister.visibility= View.VISIBLE
                        progressBar.visibility=View.INVISIBLE
                        Toast.makeText(this@Register, "Some Error Occurred", Toast.LENGTH_SHORT)
                            .show()

                    }) {
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
            val dialog = AlertDialog.Builder(this@Register)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(this@Register)
            }
            dialog.create()
            dialog.show()
        }

    }



        }




