//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.app.AlertDialog
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.fitness.app.LoginActivity
//import com.example.fitnesstracker.network.SessionManager
//
//class ProfileFragment : Fragment() {
//
//    private lateinit var sessionManager: SessionManager
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_profile, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        sessionManager = SessionManager(requireContext())
//
//        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
//        val etName = view.findViewById<EditText>(R.id.etProfileName)
//        val etWeight = view.findViewById<EditText>(R.id.etProfileWeight)
//        val btnUpdate = view.findViewById<Button>(R.id.btnUpdateProfile)
//        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
//        val btnDelete = view.findViewById<Button>(R.id.btnDeleteAccount)
//
//        tvEmail.text = sessionManager.getEmail()
//        etName.setText(sessionManager.getName())
//        etWeight.setText(sessionManager.getWeight().toString())
//
//        btnUpdate.setOnClickListener {
//            val newName = etName.text.toString().trim()
//            val newWeight = etWeight.text.toString().toDoubleOrNull()
//            if (newName.isNotEmpty()) {
//                Toast.makeText(requireContext(), "Saved locally", Toast.LENGTH_SHORT).show()
//            }
//            newWeight?.let { sessionManager.saveWeight(it) }
//        }
//
//        btnLogout.setOnClickListener {
//            sessionManager.clear()
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }
//
//        btnDelete.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("Delete Account")
//                .setMessage("Server endpoint not available in PHP bundle")
//                .setPositiveButton("OK", null)
//                .show()
//        }
//    }
//}


//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.app.AlertDialog
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.fitness.app.LoginActivity
//import com.example.fitnesstracker.network.RetrofitClient
//import kotlinx.coroutines.launch
//import com.example.fitnesstracker.network.SessionManager // IMPORT THIS
//
//class ProfileFragment : Fragment() {
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_profile, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
//        val etName = view.findViewById<EditText>(R.id.etProfileName)
//        val etWeight = view.findViewById<EditText>(R.id.etProfileWeight)
//        val etOldPass = view.findViewById<EditText>(R.id.etOldPassword)
//        val etNewPass = view.findViewById<EditText>(R.id.etNewPassword)
//
//        loadProfile(tvEmail, etName, etWeight)
//
//        view.findViewById<Button>(R.id.btnUpdateProfile).setOnClickListener {
//            val map = mapOf("name" to etName.text.toString(), "current_weight_kg" to etWeight.text.toString())
//            updateProfile(map)
//        }
//
//        view.findViewById<Button>(R.id.btnChangePass).setOnClickListener {
//            val map = mapOf("old_password" to etOldPass.text.toString(), "new_password" to etNewPass.text.toString())
//            changePass(map)
//        }
//        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
//            performLogout()
//        }
//
//        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
//            lifecycleScope.launch {
//                try { RetrofitClient.instance.logout() } catch (e: Exception) {}
//                // Navigate to login logic here
//                requireActivity().finish()
//            }
//        }
//
//        view.findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
//            AlertDialog.Builder(requireContext()).setTitle("Delete?").setMessage("Sure?").setPositiveButton("Yes") { _, _ ->
//                lifecycleScope.launch {
//                    try { RetrofitClient.instance.deleteAccount(mapOf()) } catch (e: Exception) {}
//                    requireActivity().finish()
//                }
//            }.setNegativeButton("No", null).show()
//        }
//    }
//    private fun performLogout() {
//        lifecycleScope.launch {
//            try {
//                // 2. Call API Logout
//                // RetrofitClient.instance.logout()
//
//                // 3. CRITICAL: Clear Local Cookie
//                SessionManager.clearSession()
//
//                Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
//
//                // 4. Navigate to Login
//                val intent = Intent(requireContext(), LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//                requireActivity().finish()
//
//            } catch (e: Exception) {
//                // Even if API fails, clear session locally
//                SessionManager.clearSession()
//                requireActivity().finish()
//            }
//        }
//    }
//
//    // ...
//
//    private fun loadProfile(tv: TextView, et1: EditText, et2: EditText) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.getProfile()
//                if (res.isSuccessful && res.body() != null) {
//                    val u = res.body()!!
//                    tv.text = u.email
//                    et1.setText(u.name)
//                    et2.setText(u.current_weight_kg.toString())
//                }
//            } catch (e: Exception) {}
//        }
//    }
//
//    private fun updateProfile(map: Map<String, String>) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.updateProfile(map)
//                if (res.isSuccessful) Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {}
//        }
//    }
//
//    private fun changePass(map: Map<String, String>) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.changePassword(map)
//                if (res.isSuccessful) Toast.makeText(requireContext(), "Changed", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {}
//        }
//    }
//}


package com.example.fitnesstracker.fitness.app.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.fitness.app.LoginActivity
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.fitnesstracker.network.SessionManager // IMPORT THIS


class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val etName = view.findViewById<EditText>(R.id.etProfileName)
        val etWeight = view.findViewById<EditText>(R.id.etProfileWeight)
        val etOldPass = view.findViewById<EditText>(R.id.etOldPassword)
        val etNewPass = view.findViewById<EditText>(R.id.etNewPassword)

        loadProfile(tvEmail, etName, etWeight)

        view.findViewById<Button>(R.id.btnUpdateProfile).setOnClickListener {
            val map = mapOf("name" to etName.text.toString(), "current_weight_kg" to etWeight.text.toString())
            updateProfile(map)
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            performLogout()
        }

        view.findViewById<Button>(R.id.btnChangePass).setOnClickListener {
            val map = mapOf("old_password" to etOldPass.text.toString(), "new_password" to etNewPass.text.toString())
            changePass(map)
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            lifecycleScope.launch {
                try { RetrofitClient.instance.logout() } catch (e: Exception) {}
                // Navigate to login logic here
                requireActivity().finish()
            }
        }

        view.findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Delete?").setMessage("Sure?").setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    try { RetrofitClient.instance.deleteAccount(mapOf()) } catch (e: Exception) {}
                    requireActivity().finish()
                }
            }.setNegativeButton("No", null).show()
        }
    }

    private fun loadProfile(tv: TextView, et1: EditText, et2: EditText) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getProfile()
                if (res.isSuccessful && res.body() != null) {
                    val u = res.body()!!
                    tv.text = u.email
                    et1.setText(u.name)
                    et2.setText(u.current_weight_kg.toString())
                }
            } catch (e: Exception) {}
        }
    }

    private fun updateProfile(map: Map<String, String>) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.updateProfile(map)
                if (res.isSuccessful) Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {}
        }
    }




        private fun performLogout() {
            lifecycleScope.launch {
                try {
                    // 1. Call Server Logout (Optional but recommended)
                    // RetrofitClient.instance.logout()
                    // NOTE: You might skip the API call if server is down, but clearing local is mandatory.

                    // 2. CRITICAL: Clear Local Cookie
                    SessionManager.clearSession()

                    Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()

                    // 3. Navigate to Login
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()

                } catch (e: Exception) {
                    // Even if API fails, clear session locally so user is logged out
                    SessionManager.clearSession()
                    requireActivity().finish()
                }
            }
        }
    private fun changePass(map: Map<String, String>) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.changePassword(map)
                if (res.isSuccessful) Toast.makeText(requireContext(), "Changed", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {}
        }
    }
}