package com.example.fitnesstracker.network

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "MySessionPrefs"
    private const val KEY_COOKIE = "session_cookie"

    private lateinit var prefs: SharedPreferences

    // 1. Initialize Context
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 2. Save Cookie (Called by Interceptor)
    fun saveCookie(cookie: String?) {
        if (cookie != null) {
            prefs.edit().putString(KEY_COOKIE, cookie).apply()
        }
    }

    // 3. Get Cookie (Called by Interceptor)
    fun getCookie(): String? {
        return prefs.getString(KEY_COOKIE, null)
    }

    // 4. Clear Session (Called on Logout)
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}