package com.example.fitnesstracker.fitness.app.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.network.RetrofitClient
import com.example.fitnesstracker.fitness.app.MapActivity
import com.example.fitnesstracker.model.Workout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var recentWorkouts = listOf<Workout>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Location Setup ---
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val tvLoc = view.findViewById<TextView>(R.id.tvLocation)
        val btnLoc = view.findViewById<Button>(R.id.btnGetLocation)

        btnLoc.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            } else {
                getLastLoc(tvLoc)
                // Open MapActivity
                startActivity(Intent(requireContext(), MapActivity::class.java))
            }
        }

        // --- Load Profile & History ---
        loadProfileAndHistory(view)

        setupShortcuts(view)
    }

    private fun loadProfileAndHistory(view: View) {
        lifecycleScope.launch {
            try {
                val profileRes = RetrofitClient.instance.getProfile()
                val historyRes = RetrofitClient.instance.getHistory()

                if (profileRes.isSuccessful && profileRes.body() != null) {
                    val u = profileRes.body()!!
                    view.findViewById<TextView>(R.id.tvWelcome).text =
                        "Hi ${u.name}, ready to crush today's workout?"
                }

                if (historyRes.isSuccessful && historyRes.body() != null) {
                    val workouts = historyRes.body()!!
                    recentWorkouts = workouts
                    updateStats(view, workouts)
                    setupRecentList(view, workouts)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStats(view: View, workouts: List<Workout>) {
        var totalCals = 0.0
        var totalMins = 0

        workouts.forEach {
            totalCals += it.burned_calories
            totalMins += it.time_minutes
        }

        view.findViewById<TextView>(R.id.tvTotalCals).text = totalCals.toInt().toString()
        view.findViewById<TextView>(R.id.tvTotalTime).text = totalMins.toString()

        val progress = (totalCals / 2000 * 100).toInt().coerceAtMost(100)
        view.findViewById<ProgressBar>(R.id.progressGoal).progress = progress
        view.findViewById<TextView>(R.id.tvGoalText).text = "$progress% of weekly goal"
    }


    private fun setupShortcuts(view: View) {

        fun setup(
            id: Int,
            text: String,
            icon: Int,
            bgColor: String
        ) {
            val layout = view.findViewById<LinearLayout>(id)
            val tv = layout.findViewById<TextView>(R.id.tvActivity)
            val img = layout.findViewById<ImageView>(R.id.imgActivity)

            tv.text = text
            img.setImageResource(icon)
            layout.setBackgroundColor(Color.parseColor(bgColor))
        }

        setup(R.id.btnRun, "Running", android.R.drawable.ic_menu_directions, "#E3F2FD")
        setup(R.id.btnCycle, "Cycling", android.R.drawable.ic_menu_send, "#E8F5E9")
        setup(R.id.btnWeight, "Weights", android.R.drawable.ic_menu_sort_by_size, "#F3E5F5")
        setup(R.id.btnYoga, "Yoga", android.R.drawable.ic_menu_revert, "#E1BEE7")
        setup(R.id.btnSwimming, "Swimming", android.R.drawable.ic_menu_gallery, "#B3E5FC")
        setup(R.id.btnHIIT, "HIIT", android.R.drawable.ic_menu_manage, "#FFCDD2")
        setup(R.id.btnWalking, "Walking", android.R.drawable.ic_menu_compass, "#C8E6C9")
    }

    private fun setupRecentList(view: View, workouts: List<Workout>) {
        val rv = view.findViewById<RecyclerView>(R.id.rvRecentWorkouts)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = RecentWorkoutAdapter(workouts.take(3)) // latest 3 workouts
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getLastLoc(tv: TextView) {
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) tv.text = "Lat: ${loc.latitude}, Lng: ${loc.longitude}"
            else tv.text = "Location unavailable"
        }
    }

    // --- Adapter for Recent Workouts ---
    class RecentWorkoutAdapter(private val items: List<Workout>) : RecyclerView.Adapter<RecentWorkoutAdapter.ViewHolder>() {
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvActivity: TextView = v.findViewById(android.R.id.text1)
            val tvStats: TextView = v.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val workout = items[position]
            holder.tvActivity.text = workout.activity
            holder.tvStats.text = "${workout.burned_calories.toInt()} kcal • ${workout.time_minutes} min"
        }

        override fun getItemCount() = items.size
    }
}
