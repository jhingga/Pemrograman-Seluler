package com.example.pertemuan4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pertemuan4.ui.theme.Pertemuan4Theme
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        userPreference = UserPreference(this)
        userPreference.saveUser("Dian", "dian1234", "dianprasetya772@gmail.com")

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val inputUsername = etUsername.text.toString()
            val inputPassword = etPassword.text.toString()

            if (checkLogin(inputUsername, inputPassword)) {
                val email = userPreference.getEmail()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra("EMAIL", email)
                startActivity(intent)
                finish() // Optional: close MainActivity
            } else {
                Toast.makeText(this, "Login Failed. Invalid username or password.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin(username: String, password: String): Boolean {
        val savedUsername = userPreference.getUsername()
        val savedPassword = userPreference.getPassword()
        return username == savedUsername && password == savedPassword
    }
}


