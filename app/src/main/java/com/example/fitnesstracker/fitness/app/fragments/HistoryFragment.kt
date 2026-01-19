//
//
//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.model.Workout
//import com.example.fitnesstracker.network.RetrofitClient
//import kotlinx.coroutines.launch
//
//class HistoryFragment : Fragment() {
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_history, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val rv = view.findViewById<RecyclerView>(R.id.rvWorkouts)
//        rv.layoutManager = LinearLayoutManager(requireContext())
//        loadHistory(rv)
//    }
//
//    private fun loadHistory(rv: RecyclerView) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.getHistory()
//                if (res.isSuccessful && res.body() != null) {
//                    rv.adapter = HistoryAdapter(res.body()!!)
//                }
//            } catch (e: Exception) {}
//        }
//    }
//
//    class HistoryAdapter(private val items: List<Workout>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
//        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//            val tv = v.findViewById<TextView>(android.R.id.text1)
//        }
//        override fun onCreateViewHolder(p: ViewGroup, type: Int) = ViewHolder(LayoutInflater.from(p.context).inflate(android.R.layout.simple_list_item_1, p, false))
//        override fun onBindViewHolder(h: ViewHolder, p: Int) { h.tv.text = "${items[p].activity} - ${items[p].burned_calories}" }
//        override fun getItemCount() = items.size
//    }
//}
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
    private lateinit var rvGoals: RecyclerView
    private var workoutList = mutableListOf<Workout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvWorkouts = view.findViewById(R.id.rvWorkouts)
        rvGoals = view.findViewById(R.id.rvGoals)

        rvWorkouts.layoutManager = LinearLayoutManager(requireContext())
        rvGoals.layoutManager = LinearLayoutManager(requireContext())

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

    // --- RecyclerView Adapter ---
    class HistoryAdapter(private val items: List<Workout>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvActivity: TextView = v.findViewById(android.R.id.text1)
            val tvCalories: TextView = v.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                android.R.layout.simple_list_item_2, parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val workout = items[position]
            holder.tvActivity.text = workout.activity ?: "Unknown Activity"
            holder.tvCalories.text = "Calories: ${workout.burned_calories?.toInt() ?: 0}"
        }

        override fun getItemCount(): Int = items.size
    }
}

