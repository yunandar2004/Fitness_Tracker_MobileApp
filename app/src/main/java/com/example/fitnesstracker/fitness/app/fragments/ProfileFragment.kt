//
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
//import com.example.fitnesstracker.network.SessionManager
//
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
//        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
//            performLogout()
//        }
//
//        view.findViewById<Button>(R.id.btnChangePass).setOnClickListener {
//            val map = mapOf("old_password" to etOldPass.text.toString(), "new_password" to etNewPass.text.toString())
//            changePass(map)
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
//
//
//
//        private fun performLogout() {
//            lifecycleScope.launch {
//                try {
//                    // 1. Call Server Logout (Optional but recommended)
//                    // RetrofitClient.instance.logout()
//                    // NOTE: You might skip the API call if server is down, but clearing local is mandatory.
//
//                    // CRITICAL: Clear Local Cookie
//                    SessionManager.clearSession()
//
//                    Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
//
//                    // Navigate to Login
//                    val intent = Intent(requireContext(), LoginActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    requireActivity().finish()
//
//                } catch (e: Exception) {
//                    // Even if API fails, clear session locally so user is logged out
//                    SessionManager.clearSession()
//                    requireActivity().finish()
//                }
//            }
//        }
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

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.fitness.app.LoginActivity
import com.example.fitnesstracker.network.RetrofitClient
import com.example.fitnesstracker.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileFragment : Fragment() {

    private val IMAGE_PICK_CODE = 1001
    private var selectedImageUri: Uri? = null

    private lateinit var imgProfile: ImageView
    private lateinit var tvEmail: TextView
    private lateinit var etName: EditText
    private lateinit var etWeight: EditText
    private lateinit var etOldPass: EditText
    private lateinit var etNewPass: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Views
        imgProfile = view.findViewById(R.id.imgProfile)
        tvEmail = view.findViewById(R.id.tvProfileEmail)
        etName = view.findViewById(R.id.etProfileName)
        etWeight = view.findViewById(R.id.etProfileWeight)
        etOldPass = view.findViewById(R.id.etOldPassword)
        etNewPass = view.findViewById(R.id.etNewPassword)

        // Load profile from server
        loadProfile()

        // Change profile image
        view.findViewById<Button>(R.id.btnRandomImage).setOnClickListener {
            pickImageFromGallery()
        }

        // Update profile info
        view.findViewById<Button>(R.id.btnUpdateProfile).setOnClickListener {
            updateProfile()
        }

        // Change password
        view.findViewById<Button>(R.id.btnChangePass).setOnClickListener {
            changePassword()
        }

        // Logout
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            performLogout()
        }

        // Delete account
        view.findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") { _, _ -> deleteAccount() }
                .setNegativeButton("No", null)
                .show()
        }
    }

    // ---------------- PROFILE ----------------
    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getProfile()
                if (res.isSuccessful && res.body() != null) {
                    val u = res.body()!!

                    // UI updates on main thread
                    requireActivity().runOnUiThread {
                        tvEmail.text = u.email
                        etName.setText(u.name)
                        etWeight.setText(u.current_weight_kg.toString())

                        // Load profile image without Glide
                        val imageUrl = u.profile_image
                        if (!imageUrl.isNullOrEmpty()) {
                            loadImageFromUrl(imageUrl, imgProfile)
                        } else {
                            imgProfile.setImageResource(R.drawable.ic_android_black_24dp)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Native image loader
    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val stream = java.net.URL(url).openStream()
                    BitmapFactory.decodeStream(stream)
                }
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                imageView.setImageResource(R.drawable.ic_android_black_24dp)
            }
        }
    }

    private fun updateProfile() {
        val map = mapOf(
            "name" to etName.text.toString(),
            "current_weight_kg" to etWeight.text.toString()
        )

        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.updateProfile(map)
                if (res.isSuccessful) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Upload image if selected
            selectedImageUri?.let { uploadProfileImage(it) }
        }
    }

    // ---------------- PASSWORD ----------------
    private fun changePassword() {
        val map = mapOf(
            "old_password" to etOldPass.text.toString(),
            "new_password" to etNewPass.text.toString()
        )
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.changePassword(map)
                requireActivity().runOnUiThread {
                    if (res.isSuccessful) Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error changing password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ---------------- LOGOUT ----------------
    private fun performLogout() {
        lifecycleScope.launch {
            try {
                SessionManager.clearSession()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } catch (e: Exception) {
                e.printStackTrace()
                SessionManager.clearSession()
                requireActivity().finish()
            }
        }
    }

    // ---------------- DELETE ----------------
    private fun deleteAccount() {
        lifecycleScope.launch {
            try {
                RetrofitClient.instance.deleteAccount(mapOf())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                performLogout()
            }
        }
    }

    // ---------------- IMAGE PICK ----------------
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { imgProfile.setImageURI(it) }
        }
    }

    // ---------------- IMAGE UPLOAD ----------------
    private fun uploadProfileImage(uri: Uri) {
        lifecycleScope.launch {
            try {
                val file = File(getRealPathFromURI(requireContext(), uri))
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)

                val res = RetrofitClient.instance.uploadProfileImage(body)
                requireActivity().runOnUiThread {
                    if (res.isSuccessful) Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA) ?: 0
        val path = cursor?.getString(idx) ?: ""
        cursor?.close()
        return path
    }
}
