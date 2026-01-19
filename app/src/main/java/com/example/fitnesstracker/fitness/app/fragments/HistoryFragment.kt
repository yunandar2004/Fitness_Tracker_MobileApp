//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.model.WorkoutDto
//import com.example.fitnesstracker.network.RetrofitClient
//import com.example.fitnesstracker.network.SessionManager
//import kotlinx.coroutines.launch
//
//class HistoryFragment : Fragment() {
//
//    private lateinit var sessionManager: SessionManager
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_history, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        sessionManager = SessionManager(requireContext())
//        val rvWorkouts = view.findViewById<RecyclerView>(R.id.rvWorkouts)
//        rvWorkouts.layoutManager = LinearLayoutManager(requireContext())
//
//        sessionManager.getUserId()?.let { loadHistory(rvWorkouts, it) }
//            ?: Toast.makeText(requireContext(), "Login required", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun loadHistory(rv: RecyclerView, userId: Int) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.getWorkouts(userId)
//                val body = res.body()
//                if (res.isSuccessful && body?.success == true && body.data != null) {
//                    rv.adapter = WorkoutAdapter(body.data)
//                } else {
//                    Toast.makeText(requireContext(), body?.message ?: "Unable to load", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), e.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
//
//class WorkoutAdapter(private val workouts: List<WorkoutDto>) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {
//
//    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//        val tvActivity: TextView = v.findViewById(R.id.tvWorkoutActivity)
//        val tvDetails: TextView = v.findViewById(R.id.tvWorkoutDetails)
//        val tvDate: TextView = v.findViewById(R.id.tvWorkoutDate)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_workout, parent, false))
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val workout = workouts[position]
//        holder.tvActivity.text = workout.activity
//        holder.tvDetails.text = "${workout.duration_minutes} mins � ${workout.calories} kcal"
//        holder.tvDate.text = workout.recorded_at
//    }
//
//    override fun getItemCount() = workouts.size
//}

package com.example.fitnesstracker.fitness.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.Workout
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvWorkouts)
        rv.layoutManager = LinearLayoutManager(requireContext())
        loadHistory(rv)
    }

    private fun loadHistory(rv: RecyclerView) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getHistory()
                if (res.isSuccessful && res.body() != null) {
                    rv.adapter = HistoryAdapter(res.body()!!)
                }
            } catch (e: Exception) {}
        }
    }

    class HistoryAdapter(private val items: List<Workout>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tv = v.findViewById<TextView>(android.R.id.text1)
        }
        override fun onCreateViewHolder(p: ViewGroup, type: Int) = ViewHolder(LayoutInflater.from(p.context).inflate(android.R.layout.simple_list_item_1, p, false))
        override fun onBindViewHolder(h: ViewHolder, p: Int) { h.tv.text = "${items[p].activity} - ${items[p].burned_calories}" }
        override fun getItemCount() = items.size
    }
}