package com.example.fitnesstracker.fitness.app.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
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
import java.net.URL

class ProfileFragment : Fragment() {

    private var selectedImageUri: Uri? = null

    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var etName: EditText
    private lateinit var etWeight: EditText
    private lateinit var etEmail: EditText

    private lateinit var etDOB: EditText
    private lateinit var spGender: Spinner
    private lateinit var etOldPass: EditText
    private lateinit var etNewPass: EditText

    // Modern image picker
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                imgProfile.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        imgProfile = view.findViewById(R.id.imgProfile)
        tvName = view.findViewById(R.id.tvProfileName)
        tvEmail = view.findViewById(R.id.tvProfileEmail)
        etName = view.findViewById(R.id.etProfileName)
        etWeight = view.findViewById(R.id.etProfileWeight)
        etEmail = view.findViewById(R.id.etEmail)
        etDOB = view.findViewById(R.id.etDOB)
        spGender = view.findViewById(R.id.spGender)
        etOldPass = view.findViewById(R.id.etOldPassword)
        etNewPass = view.findViewById(R.id.etNewPassword)

        // Setup Gender Spinner
        val genderOptions = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender.adapter = adapter

        // Load backend profile
        loadProfile()

        // Button listeners
        view.findViewById<Button>(R.id.btnRandomImage).setOnClickListener {
            imagePicker.launch("image/*")
        }

        view.findViewById<Button>(R.id.btnUpdateProfile).setOnClickListener {
            updateProfile()
        }

        view.findViewById<Button>(R.id.btnChangePass).setOnClickListener {
            changePassword()
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logout()
        }

        view.findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") { _, _ -> deleteAccount() }
                .setNegativeButton("No", null)
                .show()
        }
    }

    // ---------------- LOAD PROFILE ----------------
    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getProfile()
                if (res.isSuccessful && res.body() != null) {
                    val user = res.body()!!

                    // Set top name/email
                    tvName.text = user.name
                    tvEmail.text = user.email

                    // Editable fields
                    etName.setText(user.name)
                    etEmail.setText(user.email)
                    etWeight.setText(user.current_weight_kg.toString())
                    etDOB.setText(user.date_of_birth ?: "")

                    // Gender spinner selection
                    user.gender?.let { gender ->
                        val pos = (spGender.adapter as ArrayAdapter<String>).getPosition(gender)
                        if (pos >= 0) spGender.setSelection(pos)
                    }

                    // Load profile image
                    user.profile_image?.let { path ->
                        val fullUrl = if (path.startsWith("http")) path else RetrofitClient.BASE_URL + path
                        loadImage(fullUrl)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadImage(url: String) {
        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val stream = URL(url).openStream()
                    android.graphics.BitmapFactory.decodeStream(stream)
                }
                imgProfile.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imgProfile.setImageResource(R.drawable.ic_android_black_24dp)
            }
        }
    }

    // ---------------- UPDATE PROFILE ----------------
    private fun updateProfile() {
        val body = mapOf(
            "name" to etName.text.toString(),
            "current_weight_kg" to etWeight.text.toString(),
            "gender" to spGender.selectedItem.toString(),
            "email" to etEmail.text.toString(),          // <-- added email here
            "date_of_birth" to etDOB.text.toString()
        )

        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.updateProfile(body)
                if (res.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    SessionManager.saveUsername(etName.text.toString())
                    selectedImageUri?.let { uploadImage(it) }
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ---------------- CHANGE PASSWORD ----------------
    private fun changePassword() {
        val body = mapOf(
            "old_password" to etOldPass.text.toString(),
            "new_password" to etNewPass.text.toString()
        )

        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.changePassword(body)
                if (res.isSuccessful) {
                    Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show()
                    etOldPass.text.clear()
                    etNewPass.text.clear()
                } else {
                    Toast.makeText(requireContext(), "Password change failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ---------------- DELETE ACCOUNT ----------------
    private fun deleteAccount() {
        lifecycleScope.launch {
            try {
                RetrofitClient.instance.deleteAccount(emptyMap())
            } catch (e: Exception) { e.printStackTrace() }
            finally { logout() }
        }
    }

    // ---------------- LOGOUT ----------------
    private fun logout() {
        SessionManager.clearSession()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    // ---------------- IMAGE UPLOAD ----------------
    private fun uploadImage(uri: Uri) {
        lifecycleScope.launch {
            try {
                val file = uriToFile(uri)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("profile_image", file.name, requestBody)

                val res = RetrofitClient.instance.uploadProfileImage(part)
                if (res.isSuccessful) {
                    Toast.makeText(requireContext(), "Image updated", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val input = requireContext().contentResolver.openInputStream(uri)!!
        val file = File(requireContext().cacheDir, "profile_upload.jpg")
        file.outputStream().use { input.copyTo(it) }
        return file
    }
}
