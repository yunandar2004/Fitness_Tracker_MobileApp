//package com.example.fitnesstracker.network
//
//import android.content.Context
//import android.content.SharedPreferences
//
//object SessionManager {
//    private const val PREFS_NAME = "MySessionPrefs"
//    private const val KEY_COOKIE = "session_cookie"
//    private const val KEY_USERNAME = "username"
//
//
//    private lateinit var prefs: SharedPreferences
//
//    // 1. Initialize Context
////    fun init(context: GoalsFragment) {
////        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
////    }
//
//    // 2. Save Cookie (Called by Interceptor)
//    fun saveCookie(cookie: String?) {
//        if (cookie != null) {
//            prefs.edit().putString(KEY_COOKIE, cookie).apply()
//        }
//    }
//
//    // 3. Get Cookie (Called by Interceptor)
//    fun getCookie(): String? {
//        return prefs.getString(KEY_COOKIE, null)
//    }
//    // Call this after login
//    fun saveUsername(username: String) {
//        prefs.edit().putString(KEY_USERNAME, username).apply()
//    }
//
//    fun getUsername(): String {
//        return prefs.getString(KEY_USERNAME, "User") ?: "User"
//    }
//
//    // 4. Clear Session (Called on Logout)
//    fun clearSession() {
//        prefs.edit().clear().apply()
//    }
//
//    // Optional: clear session
//    fun clear() {
//        prefs.edit().clear().apply()
//    }
//}


package com.example.fitnesstracker.network

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "fitness_prefs"
    private const val KEY_COOKIE = "cookie"
    private const val KEY_USERNAME = "username"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveCookie(cookie: String) {
        prefs.edit().putString(KEY_COOKIE, cookie).apply()
    }

    fun getCookie(): String? = prefs.getString(KEY_COOKIE, null)

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, "User")

    // --- NEW: Clear all session data ---
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}


