//package com.example.fitnesstracker.fitness.app
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.databinding.ActivityLoginBinding
//import com.example.fitnesstracker.network.RetrofitClient
//import com.example.fitnesstracker.network.SessionManager
//import kotlinx.coroutines.launch
//
//class LoginActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var sessionManager: SessionManager
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        sessionManager = SessionManager(this)
//
//        binding.btnLogin.setOnClickListener {
//            val email = binding.etEmail.text.toString().trim()
//            val pass = binding.etPassword.text.toString().trim()
//            if (email.isEmpty() || pass.isEmpty()) {
//                Toast.makeText(this, "Fill email & password", Toast.LENGTH_SHORT).show()
//            } else {
//                performLogin(email, pass)
//            }
//        }
//
//        binding.tvGoRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//    }
//
//    private fun performLogin(email: String, pass: String) {
//        lifecycleScope.launch {
//            toggleLoading(true)
//            try {
//                val response = RetrofitClient.instance.login(email, pass)
//                val body = response.body()
//                if (response.isSuccessful && body?.success == true && body.data != null) {
//                    sessionManager.saveAuth(body.data)
//                    Toast.makeText(this@LoginActivity, body.message, Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    })
//                    finish()
//                } else {
//                    Toast.makeText(
//                        this@LoginActivity,
//                        body?.message ?: "Login failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(this@LoginActivity, e.localizedMessage ?: "Network error", Toast.LENGTH_SHORT).show()
//            } finally {
//                toggleLoading(false)
//            }
//        }
//    }
//
//    private fun toggleLoading(loading: Boolean) {
//        binding.btnLogin.isEnabled = !loading
//        binding.btnLogin.alpha = if (loading) 0.6f else 1f
//    }
//}

package com.example.fitnesstracker.fitness.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        RetrofitClient.init(this)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                performLogin(email, pass)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(mapOf("email" to email, "password" to password))
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DEBUG_ERROR", "Exception details", e)

                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}