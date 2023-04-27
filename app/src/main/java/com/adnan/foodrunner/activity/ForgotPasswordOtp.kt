package com.adnan.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.adnan.foodrunner.util.ConnectionManager
import com.adnan.foodrunner.R
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ForgotPasswordOtp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_otp)
        val etOTP:EditText=findViewById(R.id.etOTP)
        val btnOTPSubmit: Button
        val etPassword:EditText=findViewById(R.id.etPassword)
        val etConfirmPassword:EditText=findViewById(R.id.etConfirmPassword)
        btnOTPSubmit=findViewById(R.id.btnOTPSubmit)
        val Mobile= intent.getStringExtra("Mobile").toString()
        val url="http://13.235.250.119/v2/reset_password/fetch_result"
        val queue = Volley.newRequestQueue(this@ForgotPasswordOtp)
        if (ConnectionManager().checkConnectivity(this@ForgotPasswordOtp)) {
            btnOTPSubmit.setOnClickListener {
                var otp = etOTP.text.toString()
                var Password = etPassword.text.toString()
                var ConfirmPassword = etConfirmPassword.text.toString()
                if (otp.isEmpty() || otp.length < 4) {
                    Toast.makeText(this@ForgotPasswordOtp, "Invalid Otp", Toast.LENGTH_LONG)
                        .show()
                } else if (Password.isEmpty() || Password.length < 6 || Password.length > 15) {
                    Toast.makeText(
                        this@ForgotPasswordOtp,
                        "Password must be of 6-15 Characters",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else if (Password != ConfirmPassword) {
                    Toast.makeText(
                        this@ForgotPasswordOtp,
                        "Password and Confirm Password must be same",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", Mobile)
                    jsonParams.put("otp", otp)
                    jsonParams.put("password", Password)
                    val jsonRequest = object : JsonObjectRequest(
                        Method.POST, url, jsonParams,
                        Response.Listener {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {

                                intent = Intent(
                                    this@ForgotPasswordOtp,
                                    LoginActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                            else{
                               val errorMessage=data.getString("errorMessage")
                                Toast.makeText(this@ForgotPasswordOtp,"$errorMessage",Toast.LENGTH_LONG).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(this@ForgotPasswordOtp,"Some error occurred",Toast.LENGTH_LONG).show()
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
            val dialog = AlertDialog.Builder(this@ForgotPasswordOtp)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(this@ForgotPasswordOtp)
            }
            dialog.create()
            dialog.show()
        }

            }
}
