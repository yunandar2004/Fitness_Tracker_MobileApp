package com.example.fitnesstracker.ui.goals

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.fitness.app.LoginActivity
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

    private var editingGoalId: Int? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        tvWelcome = view.findViewById(R.id.tvWelcome)
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod)
        etGoalTitle = view.findViewById(R.id.etGoalTitle)
        etGoalTarget = view.findViewById(R.id.etGoalTarget)
        etGoalStart = view.findViewById(R.id.etGoalStart)
        etGoalDeadline = view.findViewById(R.id.etGoalDeadline)
        btnSetGoal = view.findViewById(R.id.btnSetGoal)
        btnClearForm = view.findViewById(R.id.btnClearForm)
        rvMyGoals = view.findViewById(R.id.rvMyGoals)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        RetrofitClient.init(requireContext())
        SessionManager.init(requireContext())  // <-- make sure this is called

        tvWelcome.text = "Welcome, ${SessionManager.getUsername()}"
        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                try { RetrofitClient.instance.logout() } catch (e: Exception) {}
                // Navigate to login logic here
                startActivity(Intent(requireContext(), LoginActivity::class.java))

                requireActivity().finish()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spinner
        val periods = listOf("day", "month", "duration")
        spinnerPeriod.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            periods
        )

        // RecyclerView
        adapter = GoalsAdapter(goalsList)
        rvMyGoals.layoutManager = LinearLayoutManager(requireContext())
        rvMyGoals.adapter = adapter

        fetchGoals()

        // Buttons
        btnSetGoal.setOnClickListener { saveOrUpdateGoal() }
        btnClearForm.setOnClickListener { clearForm() }
    }

    private fun saveOrUpdateGoal() {
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
                val response = if (editingGoalId == null) {
                    RetrofitClient.instance.createGoal(body)
                } else {
                    body["id"] = editingGoalId.toString()
                    RetrofitClient.instance.updateGoal(body)
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast(if (editingGoalId == null) "Goal saved" else "Goal updated")
                        fetchGoals()
                        clearForm()
                        editingGoalId = null
                        btnSetGoal.text = "Save Goal"
                    } else {
                        showToast("Failed to save/update goal")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showToast("Error: ${e.message}") }
            }
        }
    }

    private fun fetchGoals() {
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
                withContext(Dispatchers.Main) { showToast("Error: ${e.message}") }
            }
        }
    }



    private fun resetGoal(goal: Goal) {
        val body = mapOf("id" to goal.id.toString())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.resetGoal(body)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("Goal deleted")
                        fetchGoals() // Refresh the list after deletion
                    } else showToast("Failed to delete goal")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showToast("Error: ${e.message}") }
            }
        }
    }
    private fun deleteGoal(goal: Goal) {
        val body = mapOf("id" to goal.id.toString())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.deleteGoal(body)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("Goal deleted")

                        val position = goalsList.indexOf(goal)
                        if (position != -1) {
                            goalsList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }

                    } else {
                        showToast("Failed to delete goal")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            }
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
//                startActivity(Intent(this, LoginActivity::class.java))


            } catch (e: Exception) {
                // Even if API fails, clear session locally so user is logged out
                SessionManager.clearSession()
                requireActivity().finish()
            }
        }
    }


    private fun clearForm() {
        etGoalTitle.text?.clear()
        etGoalTarget.text?.clear()
        etGoalStart.text?.clear()
        etGoalDeadline.text?.clear()
        btnSetGoal.text = "Save Goal"
        editingGoalId = null
    }
    private fun showDeleteDialog(goal: Goal) {
        AlertDialog.Builder(requireContext())
            .setTitle("Your Goal rested")
            .setMessage("Are you sure you want to reset this goal?")
            .setCancelable(false)
            .setPositiveButton("Reset") { _, _ ->
                deleteGoal(goal)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    inner class GoalsAdapter(private val goals: List<Goal>) :
        RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {
        inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTitle: TextView = itemView.findViewById(R.id.tvGoalTitle)
            val tvType: TextView = itemView.findViewById(R.id.tvGoalType)
            val tvDates: TextView = itemView.findViewById(R.id.tvGoalDates)
            val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
            val tvProgressText: TextView = itemView.findViewById(R.id.tvProgressText)
            val btnReset: Button = itemView.findViewById(R.id.btnResetGoal)
            val btnEdit: Button = itemView.findViewById(R.id.btnEditGoal)
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

            holder.btnReset.setOnClickListener {
                showDeleteDialog(goal)
            }
            holder.btnEdit.setOnClickListener { editGoal(goal) }
        }

        override fun getItemCount(): Int = goals.size
        private fun editGoal(goal: Goal) {
            editingGoalId = goal.id
            etGoalTitle.setText(goal.title)
            val periods = listOf("day", "month", "duration")
            spinnerPeriod.setSelection(periods.indexOf(goal.period).coerceAtLeast(0))
            etGoalTarget.setText(goal.target_value?.toString() ?: "")
            etGoalStart.setText(goal.start_date ?: "")
            etGoalDeadline.setText(goal.end_date ?: "")
            btnSetGoal.text = "Update Goal"
        }
    }

}
