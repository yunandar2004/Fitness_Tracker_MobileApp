

package com.example.fitnesstracker.fitness.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.Workout
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var rvWorkouts: RecyclerView
    private var workoutList = mutableListOf<Workout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvWorkouts = view.findViewById(R.id.rvWorkouts)
        rvWorkouts.layoutManager = LinearLayoutManager(requireContext())
        rvWorkouts.adapter = HistoryAdapter(workoutList)

        loadHistory()
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getHistory()
                if (response.isSuccessful && response.body() != null) {
                    workoutList.clear()
                    workoutList.addAll(response.body()!!)
                    rvWorkouts.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Failed to load history", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error fetching history", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class HistoryAdapter(private val items: List<Workout>) :
        RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvWorkoutName: TextView = v.findViewById(R.id.tvWorkoutName)
            val tvWorkoutDate: TextView = v.findViewById(R.id.tvWorkoutDate)
            val tvWorkoutDuration: TextView = v.findViewById(R.id.tvWorkoutDuration)
            val tvCalories: TextView = v.findViewById(R.id.tvCalories)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_workout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val workout = items[position]

            holder.tvWorkoutName.text = workout.activity
            holder.tvWorkoutDate.text = workout.created_at ?: "-"
            holder.tvWorkoutDuration.text = "${workout.time_minutes} min"
            holder.tvCalories.text = "🔥 ${workout.burned_calories.toInt()} kcal"
        }

        override fun getItemCount(): Int = items.size
    }
}
