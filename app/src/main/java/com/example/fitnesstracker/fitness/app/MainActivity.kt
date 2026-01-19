//package com.example.fitnesstracker.fitness.app
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.fitness.app.fragments.*
//import com.example.fitnesstracker.network.SessionManager
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var sessionManager: SessionManager
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        sessionManager = SessionManager(this)
//        if (!sessionManager.isLoggedIn()) {
//            startActivity(Intent(this, LoginActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            })
//            finish()
//            return
//        }
//
//        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
//
//        bottomNav.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_home -> replaceFragment(HomeFragment())
//                R.id.nav_calculate -> replaceFragment(CalculateFragment())
//                R.id.nav_history -> replaceFragment(HistoryFragment())
//                R.id.nav_goals -> replaceFragment(GoalsFragment())
//                R.id.nav_profile -> replaceFragment(ProfileFragment())
//            }
//            true
//        }
//
//        replaceFragment(HomeFragment())
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, fragment)
//            .commit()
//    }
//}

//package com.example.fitnesstracker.fitness.app
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.model.User
//import com.example.fitnesstracker.network.RetrofitClient
//import com.example.fitnesstracker.fitness.app.fragments.*
//import kotlinx.coroutines.launch
//
//class MainActivity: AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        try { RetrofitClient.init(this) } catch (e: Exception) {}
//
//        checkUserRole()
//
//        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
//        bottomNav.setOnItemSelectedListener {
//            when(it.itemId) {
//                R.id.nav_home -> replaceFragment(HomeFragment())
//                R.id.nav_calculate -> replaceFragment(CalculateFragment())
//                R.id.nav_history -> replaceFragment(HistoryFragment())
//                R.id.nav_goals -> replaceFragment(GoalsFragment())
//                R.id.nav_profile -> replaceFragment(ProfileFragment())
//            }
//            true
//        }
//        replaceFragment(HomeFragment())
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, fragment)
//            .commit()
//    }
//
//    private fun checkUserRole() {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.getProfile()
//                if (res.isSuccessful && res.body() != null) {
//                    val user = res.body()!!
//                    if (user.role == "admin") {
//                        Toast.makeText(this@MainActivity, "Admin Detected", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this@MainActivity, AdminActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//            } catch (e: Exception) {}
//        }
//    }
//}

//package com.example.fitnesstracker.fitness.app
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.model.User
//import com.example.fitnesstracker.network.RetrofitClient
//import com.example.fitnesstracker.fitness.app.fragments.*
//import kotlinx.coroutines.launch
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // IMPORTANT: Always init RetrofitClient
//        try { RetrofitClient.init(this) } catch (e: Exception) {}
//
//        checkUserRole()
//
//        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
//        bottomNav.setOnItemSelectedListener { item ->
//            // No finish() calls here. Use fragment backstack.
//            when(item.itemId) {
//                R.id.nav_home -> replaceFragment(HomeFragment())
//                R.id.nav_calculate -> replaceFragment(CalculateFragment())
//                R.id.nav_history -> replaceFragment(HistoryFragment())
//                R.id.nav_goals -> replaceFragment(GoalsFragment())
//                R.id.nav_profile -> replaceFragment(ProfileFragment())
//            }
//            true
//        }
//        replaceFragment(HomeFragment()) // Load Home by default
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, fragment)
//            .commit()
//    }
//
//    private fun checkUserRole() {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.getProfile()
//                if (res.isSuccessful && res.body() != null) {
//                    val user = res.body()!!
//                    if (user.role == "admin") {
//                        // Admin goes to dedicated activity
//                        Toast.makeText(this@MainActivity, "Admin Redirect", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this@MainActivity, AdminActivity::class.java)
//                        startActivity(intent)
//                        finish() // Only finish HERE (when going to Admin)
//                    }
//                }
//            } catch (e: Exception) {
//                // If server fails, do not close app. Just stay on dashboard.
//            }
//        }
//    }
//}

package com.example.fitnesstracker.fitness.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.User
import com.example.fitnesstracker.network.RetrofitClient
import com.example.fitnesstracker.fitness.app.fragments.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try { RetrofitClient.init(this) } catch (e: Exception) {}

        checkUserRole()

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_calculate -> replaceFragment(CalculateFragment())
                R.id.nav_history -> replaceFragment(HistoryFragment())
                R.id.nav_goals -> replaceFragment(GoalsFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun checkUserRole() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getProfile()
                if (res.isSuccessful && res.body() != null) {
                    val user = res.body()!!
                    if (user.role == "admin") {
                        Toast.makeText(this@MainActivity, "Admin Detected", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, AdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } catch (e: Exception) {}
        }
    }
}