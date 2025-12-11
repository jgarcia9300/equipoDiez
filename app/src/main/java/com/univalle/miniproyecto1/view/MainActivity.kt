package com.univalle.miniproyecto1.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.univalle.miniproyecto1.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val shared = getSharedPreferences("shared", MODE_PRIVATE)
        val isLoggedIn = shared.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }


        setContentView(R.layout.activity_main)
    }
}
