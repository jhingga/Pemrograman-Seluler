package com.example.pertemuan4

import android.content.Context
import android.content.SharedPreferences

class UserPreference(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_EMAIL = "email"
    }

    fun saveUser(username: String, password: String, email: String) {
        val editor = preferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getUsername(): String? {
        return preferences.getString(KEY_USERNAME, null)
    }

    fun getPassword(): String? {
        return preferences.getString(KEY_PASSWORD, null)
    }

    fun getEmail(): String? {
        return preferences.getString(KEY_EMAIL, null)
    }
}