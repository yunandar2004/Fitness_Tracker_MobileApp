////package com.example.fitnesstracker.fitness.app.fragments
////
////import android.Manifest
////import android.content.pm.PackageManager
////import android.location.Location
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.Button
////import android.widget.TextView
////import android.widget.Toast
////import androidx.core.content.ContextCompat
////import androidx.fragment.app.Fragment
////import androidx.lifecycle.lifecycleScope
////import com.example.fitnesstracker.R
////import com.example.fitnesstracker.network.RetrofitClient
////import com.example.fitnesstracker.network.SessionManager
////import com.google.android.gms.location.FusedLocationProviderClient
////import com.google.android.gms.location.LocationServices
////import kotlinx.coroutines.launch
////
////class HomeFragment : Fragment() {
////
////    private lateinit var fusedLocationClient: FusedLocationProviderClient
////    private lateinit var sessionManager: SessionManager
////
////    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
////        return inflater.inflate(R.layout.fragment_home, container, false)
////    }
////
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////        sessionManager = SessionManager(requireContext())
////
////        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
////        val tvLocation = view.findViewById<TextView>(R.id.tvLocation)
////        val tvProgress = view.findViewById<TextView>(R.id.tvProgress)
////        val btnGetLocation = view.findViewById<Button>(R.id.btnGetLocation)
////
////        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
////
////        val name = sessionManager.getName().ifEmpty { "there" }
////        tvWelcome.text = "Welcome, $name"
////
////        sessionManager.getUserId()?.let { userId ->
////            lifecycleScope.launch {
////                try {
////                    val response = RetrofitClient.instance.getProgress(userId)
////                    val body = response.body()
////                    if (response.isSuccessful && body?.success == true && body.data != null) {
////                        val summary = body.data
////                        tvProgress.text = "Workouts: ${summary.total_workouts}\nMinutes: ${summary.total_minutes}\nCalories: ${summary.calories}"
////                    } else {
////                        tvProgress.text = body?.message ?: "Unable to load progress"
////                    }
////                } catch (e: Exception) {
////                    tvProgress.text = "Progress unavailable"
////                }
////            }
////        } ?: run {
////            tvProgress.text = "Please login"
////        }
////
////        btnGetLocation.setOnClickListener {
////            checkPermissionsAndGetLocation(tvLocation)
////        }
////    }
////
////    private fun checkPermissionsAndGetLocation(tvLocation: TextView) {
////        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
////            != PackageManager.PERMISSION_GRANTED) {
////
////            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
////        } else {
////            getLastLocation(tvLocation)
////        }
////    }
////
////    @Suppress("DEPRECATION")
////    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
////        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////            view?.findViewById<TextView>(R.id.tvLocation)?.let { getLastLocation(it) }
////        } else {
////            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
////        }
////    }
////
////    @Suppress("MissingPermission")
////    private fun getLastLocation(tvLocation: TextView) {
////        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
////            if (location != null) {
////                val lat = location.latitude
////                val lng = location.longitude
////                tvLocation.text = "Location: $lat, $lng"
////            } else {
////                tvLocation.text = "Location: Unable to fetch (GPS might be off)"
////            }
////        }
////    }
////}
//
//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.network.RetrofitClient
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import kotlinx.coroutines.launch
//
//class HomeFragment : Fragment() {
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_home, container, false)
//    }
//
//    @Suppress("DEPRECATION")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
//        val tvLoc = view.findViewById<TextView>(R.id.tvLocation)
//        val btnLoc = view.findViewById<Button>(R.id.btnGetLocation)
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.getProfile()
//                if (res.isSuccessful && res.body() != null) {
//                    val u = res.body()!!
//                    tvWelcome.text = "Welcome, ${u.name}"
//                }
//            } catch (e: Exception) {}
//        }
//
//        btnLoc.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
//            } else {
//                getLastLoc(tvLoc)
//            }
//        }
//    }
//
//    @Suppress("DEPRECATION")
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            getLastLoc(requireView().findViewById(R.id.tvLocation))
//        }
//    }
//
//    private fun getLastLoc(tv: TextView) {
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) tv.text = "Lat: ${location.latitude}, Lng: ${location.longitude}"
//            else tv.text = "Loc: Unavailable"
//        }
//    }
//}
package com.example.fitnesstracker.fitness.app.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.Workout
import com.example.fitnesstracker.network.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var recentWorkouts = listOf<Workout>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Init Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val tvLoc = view.findViewById<TextView>(R.id.tvLocation)
        val btnLoc = view.findViewById<Button>(R.id.btnGetLocation)

        btnLoc.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            } else {
                getLastLoc(tvLoc)
            }
        }

        // 2. Load Data
        loadProfileAndHistory(view)

        // 3. Setup Shortcuts
        setupShortcuts(view)
    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Init Location
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        val tvLoc = view.findViewById<TextView>(R.id.tvLocation)
//        val btnLoc = view.findViewById<Button>(R.id.btnGetLocation)
//
//        btnLoc.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
//            } else {
//                getLastLoc(tvLoc)
//            }
//        }
//
//        // LOAD HISTORY TO WATCH OVERALL CODES
//        loadProfileAndHistory(view)
//    }

    private fun loadProfileAndHistory(view: View) {
        lifecycleScope.launch {
            try {
                // Parallel Fetching
                val profileRes = RetrofitClient.instance.getProfile()
                val historyRes = RetrofitClient.instance.getHistory()

                if (profileRes.isSuccessful && profileRes.body() != null) {
                    val u = profileRes.body()!!
                    view.findViewById<TextView>(R.id.tvWelcome).text = "Hi ${u.name}, ready to crush today's workout?"
                }

                if (historyRes.isSuccessful && historyRes.body() != null) {
                    val workouts = historyRes.body()!!
                    updateStats(view, workouts)
                    setupRecentList(view, workouts)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStats(view: View, workouts: List<Workout>) {
        // Calculate Total Calories from History
        var totalCals = 0.0
        var totalMins = 0

        workouts.forEach {
            totalCals += it.burned_calories
            totalMins += it.time_minutes
        }

        // Update UI
        view.findViewById<TextView>(R.id.tvTotalCals).text = totalCals.toInt().toString()
        view.findViewById<TextView>(R.id.tvTotalTime).text = totalMins.toString()

        // Mock Progress (Goal 2000 cals)
        val progress = (totalCals / 2000 * 100).toInt().coerceAtMost(100)
        view.findViewById<ProgressBar>(R.id.progressGoal).progress = progress
        view.findViewById<TextView>(R.id.tvGoalText).text = "$progress% of weekly goal"
    }
    // ... rest of class (setupShortcuts, etc) remains same

//    private fun loadProfileAndHistory(view: View) {
//        lifecycleScope.launch {
//            try {
//                // Parallel Fetching
//                val profileRes = RetrofitClient.instance.getProfile()
//                val historyRes = RetrofitClient.instance.getHistory()
//
//                if (profileRes.isSuccessful && profileRes.body() != null) {
//                    val user = profileRes.body()!!
//                    view.findViewById<TextView>(R.id.tvWelcome).text = "Hi ${user.name}, ready to crush today's workout?"
//                }
//
//                if (historyRes.isSuccessful && historyRes.body() != null) {
//                    recentWorkouts = historyRes.body()!!
//                    updateStats(view, recentWorkouts)
//                    setupRecentList(view, recentWorkouts)
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun updateStats(view: View, workouts: List<Workout>) {
//        // Calculate Stats
//        var totalCals = 0.0
//        var totalMins = 0
//
//        workouts.forEach {
//            totalCals += it.burned_calories
//            totalMins += it.time_minutes
//        }
//
//        // Update UI (Steps mocked)
//        view.findViewById<TextView>(R.id.tvTotalCals).text = totalCals.toInt().toString()
//        view.findViewById<TextView>(R.id.tvTotalTime).text = totalMins.toString()
//
//        // Mock Progress (Goal 2000 cals)
//        val progress = (totalCals / 2000 * 100).toInt().coerceAtMost(100)
//        view.findViewById<ProgressBar>(R.id.progressGoal).progress = progress
//        view.findViewById<TextView>(R.id.tvGoalText).text = "$progress% of weekly goal"
//    }

    private fun setupShortcuts(view: View) {
        // Helper to open specific tab
        val navigate = {
            // You can pass intent to main with specific tab, or just open main
            // For simplicity, we assume Main opens last active tab or default
        }

        view.findViewById<View>(R.id.btnRun).setOnClickListener { showToast("Starting Running Tracker") }
        view.findViewById<View>(R.id.btnCycle).setOnClickListener { showToast("Starting Cycling Tracker") }
        view.findViewById<View>(R.id.btnWeight).setOnClickListener { showToast("Starting Weights Tracker") }
        view.findViewById<View>(R.id.btnYoga).setOnClickListener { showToast("Starting Yoga Tracker") }
    }

    private fun setupRecentList(view: View, workouts: List<Workout>) {
        val rv = view.findViewById<RecyclerView>(R.id.rvRecentWorkouts)
        rv.layoutManager = LinearLayoutManager(requireContext())
        // Show only last 3
        rv.adapter = RecentWorkoutAdapter(workouts.take(3))
    }

    @Suppress("DEPRECATION")
    private fun getLastLoc(tv: TextView) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) tv.text = "Lat: ${location.latitude}, Lng: ${location.longitude}"
            else tv.text = "Loc: Unavailable"
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    // --- Simple Adapter for Recent Workouts ---
    class RecentWorkoutAdapter(private val items: List<Workout>) : RecyclerView.Adapter<RecentWorkoutAdapter.ViewHolder>() {
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvActivity = v.findViewById<TextView>(android.R.id.text1)
            val tvStats = v.findViewById<TextView>(android.R.id.text2)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false))
        }
        override fun onBindViewHolder(h: ViewHolder, p: Int) {
            h.tvActivity.text = items[p].activity
            h.tvStats.text = "${items[p].burned_calories.toInt()} kcal • ${items[p].time_minutes} min"
        }
        override fun getItemCount() = items.size
    }
}