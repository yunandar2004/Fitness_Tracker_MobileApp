
package com.example.fitnesstracker.fitness.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        RetrofitClient.init(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val etDob = findViewById<EditText>(R.id.etDob)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        val btnReg = findViewById<Button>(R.id.btnRegister)
        val tvGoLogin = findViewById<TextView>(R.id.tvGoLogin)


        ArrayAdapter.createFromResource(
            this, R.array.gender_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter
        }

        btnReg.setOnClickListener {
            val map = mapOf<String, String>(
                "name" to etName.text.toString(),
                "email" to etEmail.text.toString(),
                "password" to etPass.text.toString(),
                "date_of_birth" to etDob.text.toString(),
                "current_weight_kg" to etWeight.text.toString(),
                "gender" to spinnerGender.selectedItem.toString().lowercase()
            )
            performRegister(map)
        }
        tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun performRegister(map: Map<String, String>) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.register(map)
                if (res.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registered! Please Login", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DEBUG_ERROR", "Exception details", e)

                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}