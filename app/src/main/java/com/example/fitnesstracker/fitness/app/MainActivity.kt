package com.example.fitnesstracker.fitness.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.network.RetrofitClient
import com.example.fitnesstracker.fitness.app.fragments.*
import com.example.fitnesstracker.ui.goals.GoalsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RetrofitClient safely
        try { RetrofitClient.init(this) } catch (e: Exception) {}

        checkUserRole()

        // Initialize BottomNavigationView
        bottomNav = findViewById(R.id.bottomNav)
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

        // Initialize FloatingActionButton
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            // Only navigate to GoalsFragment if not already there
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (currentFragment !is GoalsFragment) {
                replaceFragmentWithAnimation(GoalsFragment())
            }
        }

        // Set default fragment to Home
        replaceFragment(HomeFragment())
    }

    // Function to replace fragments without animation
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Function to replace fragments with a slide animation
    private fun replaceFragmentWithAnimation(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Check user role from API and redirect if admin
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
            } catch (e: Exception) {
                // Optional: Log error
            }
        }
    }
}
