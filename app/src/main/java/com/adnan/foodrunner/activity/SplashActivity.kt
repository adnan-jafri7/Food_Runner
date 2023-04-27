package com.adnan.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.adnan.foodrunner.R
import java.net.HttpURLConnection

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            val startAct= Intent(this@SplashActivity,
                LoginActivity::class.java)
            startActivity(startAct)},1000)
        setContentView(R.layout.activity_splash)
        

    }
    override fun onPause(){
        super.onPause()
        finish()
    }
}
