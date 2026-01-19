//package com.example.fitnesstracker.fitness.app
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.network.RetrofitClient
//import com.example.fitnesstracker.network.SessionManager
//import kotlinx.coroutines.launch
//import java.util.*
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var sessionManager: SessionManager
//    private lateinit var etDob: EditText
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//
//        sessionManager = SessionManager(this)
//
//        val etName = findViewById<EditText>(R.id.etName)
//        val etEmail = findViewById<EditText>(R.id.etEmail)
//        val etPassword = findViewById<EditText>(R.id.etPassword)
//        etDob = findViewById(R.id.etDob)
//        val etWeight = findViewById<EditText>(R.id.etWeight)
//        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
//        val btnRegister = findViewById<Button>(R.id.btnRegister)
//
//        // Gender spinner
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.gender_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinnerGender.adapter = adapter
//        }
//
//        // Date picker for DOB
//        etDob.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//
//            val dpd = DatePickerDialog(this, { _, y, m, d ->
//                etDob.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
//            }, year, month, day)
//            dpd.show()
//        }
//
//        btnRegister.setOnClickListener {
//            val name = etName.text.toString().trim()
//            val email = etEmail.text.toString().trim()
//            val password = etPassword.text.toString().trim()
//            val dob = etDob.text.toString().trim()  // YYYY-MM-DD
//            val weight = etWeight.text.toString().trim().ifEmpty { null }
//            val gender = spinnerGender.selectedItem.toString().lowercase()
//
//            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
//                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            performRegister(name, email, password, dob, weight, gender)
//        }
//    }
//
//    private fun performRegister(
//        name: String,
//        email: String,
//        password: String,
//        dob: String,
//        weight: String?,
//        gender: String?
//    ) {
//        lifecycleScope.launch {
//            try {
//                val response = RetrofitClient.instance.register(
//                    name, email, password, dob, weight, gender
//                )
//
//                // Log raw HTTP info
//                android.util.Log.d("API_HTTP", response.raw().toString())
//
//                // Log raw body for debugging
//                val rawError = response.errorBody()?.string()
//                val rawBody = response.body()?.let { com.google.gson.Gson().toJson(it) }
//                android.util.Log.d("API_RAW_ERROR", rawError ?: "no error body")
//                android.util.Log.d("API_RAW_BODY", rawBody ?: "no body")
//
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    if (body?.success == true && body.data != null) {
//                        val payload = body.data
//                        // Save token and optional weight
//                        sessionManager.saveAuth(payload, weight?.toDoubleOrNull())
//                        Toast.makeText(this@RegisterActivity, body.message, Toast.LENGTH_SHORT).show()
//                        finish()
//                    } else {
//                        Toast.makeText(
//                            this@RegisterActivity,
//                            body?.message ?: "Registration failed",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                } else {
//                    Toast.makeText(
//                        this@RegisterActivity,
//                        "Server error: ${response.code()}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(this@RegisterActivity, e.localizedMessage ?: "Network error", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }
//}
package com.example.fitnesstracker.fitness.app

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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