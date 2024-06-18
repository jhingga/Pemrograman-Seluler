package com.example.sesa

import DatabaseHelper
import SessionManager
import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var cbRemember: CheckBox
    private lateinit var btnLoginRegister: Button
    private lateinit var tabLogin: Button
    private lateinit var tabRegister: Button

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        cbRemember = findViewById(R.id.cbRemember)
        btnLoginRegister = findViewById(R.id.btnLoginRegister)
        tabLogin = findViewById(R.id.tabLogin)
        tabRegister = findViewById(R.id.tabRegister)

        // Handle tab switching
        tabLogin.setOnClickListener {
            switchToLoginMode()
        }
        tabRegister.setOnClickListener {
            switchToRegisterMode()
        }

        // Handle login/register button click
        btnLoginRegister.setOnClickListener {
            if (isLoginMode) {
                login()
            } else {
                register()
            }
        }
    }

    private fun switchToLoginMode() {
        isLoginMode = true
        etEmail.visibility = android.view.View.GONE
        etConfirmPassword.visibility = android.view.View.GONE
        btnLoginRegister.text = "Login"
        tabLogin.setTextColor(resources.getColor(R.color.colorPrimary))
        tabRegister.setTextColor(resources.getColor(android.R.color.darker_gray))
    }

    private fun switchToRegisterMode() {
        isLoginMode = false
        etEmail.visibility = android.view.View.VISIBLE
        etConfirmPassword.visibility = android.view.View.VISIBLE
        btnLoginRegister.text = "Register"
        tabLogin.setTextColor(resources.getColor(android.R.color.darker_gray))
        tabRegister.setTextColor(resources.getColor(R.color.colorPrimary))
    }

    private fun login() {
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            arrayOf(DatabaseHelper.COLUMN_EMAIL, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASSWORD),
            "${DatabaseHelper.COLUMN_USERNAME}=? AND ${DatabaseHelper.COLUMN_PASSWORD}=?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL))
            if (cbRemember.isChecked) {
                sessionManager.saveLogin(email)
            }
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            // Navigate to next activity
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }

    private fun register() {
        val username = etUsername.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_EMAIL, email)
            put(DatabaseHelper.COLUMN_USERNAME, username)
            put(DatabaseHelper.COLUMN_PASSWORD, password)
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            switchToLoginMode()
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
        }
    }
}