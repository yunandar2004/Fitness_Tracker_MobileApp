
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
import com.example.fitnesstracker.network.SessionManager
import com.example.fitnesstracker.ui.goals.GoalsFragment
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