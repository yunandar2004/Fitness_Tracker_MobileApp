
package com.example.fitnesstracker.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.Goal
import com.example.fitnesstracker.network.RetrofitClient
import com.example.fitnesstracker.network.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoalsFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var spinnerPeriod: Spinner
    private lateinit var etGoalTitle: EditText
    private lateinit var etGoalTarget: EditText
    private lateinit var etGoalStart: EditText
    private lateinit var etGoalDeadline: EditText
    private lateinit var btnSetGoal: Button
    private lateinit var btnClearForm: Button
    private lateinit var rvMyGoals: RecyclerView

    private val goalsList = mutableListOf<Goal>()
    private lateinit var adapter: GoalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        // Initialize views
        tvWelcome = view.findViewById(R.id.tvWelcome)
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod)
        etGoalTitle = view.findViewById(R.id.etGoalTitle)
        etGoalTarget = view.findViewById(R.id.etGoalTarget)
        etGoalStart = view.findViewById(R.id.etGoalStart)
        etGoalDeadline = view.findViewById(R.id.etGoalDeadline)
        btnSetGoal = view.findViewById(R.id.btnSetGoal)
        btnClearForm = view.findViewById(R.id.btnClearForm)
        rvMyGoals = view.findViewById(R.id.rvMyGoals)

        // Initialize SessionManager
        RetrofitClient.init(requireContext())

        // Reference the TextView
        tvWelcome.text = "Welcome, ${SessionManager.getUsername()}"
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val profileRes = RetrofitClient.instance.getProfile()
//        val u = profileRes.body()

        // Welcome
        tvWelcome.text = "Welcome, ${SessionManager.getUsername()}"

        // Spinner
        val periods = listOf("day", "month", "duration")
        spinnerPeriod.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, periods)

        // RecyclerView
        adapter = GoalsAdapter(goalsList)
        rvMyGoals.layoutManager = LinearLayoutManager(requireContext())
        rvMyGoals.adapter = adapter

        fetchProfile()
        // Load goals
        fetchGoals()

        // Buttons
        btnSetGoal.setOnClickListener { saveGoal() }
        btnClearForm.setOnClickListener { clearForm() }
    }

    /** Fetch goals from server */
    private fun fetchGoals() {
        //        val profileRes = RetrofitClient.instance.getProfile()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getGoals()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    goalsList.clear()
                    goalsList.addAll(list)
                    withContext(Dispatchers.Main) { adapter.notifyDataSetChanged() }
                } else showToast("Failed to load goals")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }
    private fun fetchProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getProfile()
                if (response.isSuccessful) {
                    val user = response.body()
                    withContext(Dispatchers.Main) {
                        tvWelcome.text = "Welcome, ${user?.name ?: "User"}"
                        // Optionally save username in SessionManager
                        user?.name?.let { SessionManager.saveUsername(it) }
                    }
                } else {
                    showToast("Failed to load profile")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }


    /** Save a new goal */
    private fun saveGoal() {
        val title = etGoalTitle.text.toString().trim()
        val period = spinnerPeriod.selectedItem.toString()
        val target = etGoalTarget.text.toString().trim()
        val start = etGoalStart.text.toString().trim()
        val end = etGoalDeadline.text.toString().trim()

        if (title.isEmpty() || target.isEmpty()) {
            showToast("Please fill title and target")
            return
        }

        val body = mutableMapOf(
            "title" to title,
            "period" to period,
            "duration_minutes" to target,
            "start_date" to start,
            "end_date" to end
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createGoal(body)
                if (response.isSuccessful) {
                    showToast("Goal saved")
                    fetchGoals()
                    clearForm()
                } else showToast("Failed to save goal")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    /** Reset a goal */
    private fun resetGoal(goal: Goal) {
        val body = mapOf("id" to goal.id.toString())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.resetGoal(body)
                if (response.isSuccessful) {
                    showToast("Goal reset")
                    fetchGoals()
                } else showToast("Failed to reset goal")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    /** Clear form inputs */
    private fun clearForm() {
        etGoalTitle.text?.clear()
        etGoalTarget.text?.clear()
        etGoalStart.text?.clear()
        etGoalDeadline.text?.clear()
    }

    /** Show toast */
    private fun showToast(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    /** RecyclerView Adapter */
    inner class GoalsAdapter(private val goals: List<Goal>) :
        RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

        inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTitle: TextView = itemView.findViewById(R.id.tvGoalTitle)
            val tvType: TextView = itemView.findViewById(R.id.tvGoalType)
            val tvDates: TextView = itemView.findViewById(R.id.tvGoalDates)
            val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
            val tvProgressText: TextView = itemView.findViewById(R.id.tvProgressText)
            val btnReset: Button = itemView.findViewById(R.id.btnResetGoal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_goal, parent, false)
            return GoalViewHolder(view)
        }

        override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
            val goal = goals[position]

            holder.tvTitle.text = goal.title
            holder.tvType.text = goal.period.replaceFirstChar { it.uppercase() }
            holder.tvDates.text = "${goal.start_date ?: "N/A"} → ${goal.end_date ?: "N/A"}"

            val progress = if ((goal.target_value ?: 0) > 0)
                (goal.current_value ?: 0) * 100 / goal.target_value!!
            else 0
            holder.progressBar.progress = progress
            holder.tvProgressText.text =
                "Progress: ${goal.current_value ?: 0} / ${goal.target_value ?: 0}"

            holder.btnReset.setOnClickListener { resetGoal(goal) }
        }

        override fun getItemCount(): Int = goals.size
    }
}
