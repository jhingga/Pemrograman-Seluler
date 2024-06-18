package com.example.pertemuan4


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val email = intent.getStringExtra("EMAIL")
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = "Welcome Back, $email"
    }
}
