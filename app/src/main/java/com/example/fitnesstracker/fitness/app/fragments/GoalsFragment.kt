//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.model.GoalDto
//import com.example.fitnesstracker.network.RetrofitClient
//import com.example.fitnesstracker.network.SessionManager
//import kotlinx.coroutines.launch
//
//class GoalsFragment : Fragment() {
//    private lateinit var sessionManager: SessionManager
//    private lateinit var rvGoals: RecyclerView
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_goals, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        sessionManager = SessionManager(requireContext())
//
//        val etTitle = view.findViewById<EditText>(R.id.etGoalTitle)
//        val etTarget = view.findViewById<EditText>(R.id.etGoalTarget)
//        val etDeadline = view.findViewById<EditText>(R.id.etGoalDeadline)
//        val etNotes = view.findViewById<EditText>(R.id.etGoalNotes)
//        val spinnerPeriod = view.findViewById<Spinner>(R.id.spinnerPeriod)
//        val btnSet = view.findViewById<Button>(R.id.btnSetGoal)
//        rvGoals = view.findViewById(R.id.rvMyGoals)
//
//        rvGoals.layoutManager = LinearLayoutManager(requireContext())
//
//        btnSet.setOnClickListener {
//            val userId = sessionManager.getUserId()
//            if (userId == null) {
//                Toast.makeText(requireContext(), "Login required", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val title = etTitle.text.toString().trim()
//            val target = etTarget.text.toString().trim()
//            val deadline = etDeadline.text.toString().trim()
//            if (title.isEmpty() || target.isEmpty() || deadline.isEmpty()) {
//                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val notes = etNotes.text.toString().trim().ifEmpty { null }
//            createGoal(userId, title, spinnerPeriod.selectedItem.toString(), target, deadline, notes)
//        }
//
//        sessionManager.getUserId()?.let { loadGoals(it) }
//    }
//
//    private fun createGoal(
//        userId: Int,
//        title: String,
//        type: String,
//        targetValue: String,
//        deadline: String,
//        notes: String?
//    ) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.saveGoal(
//                    userId = userId,
//                    type = type.lowercase(),
//                    targetValue = targetValue,
//                    deadline = deadline,
//                    title = title,
//                    notes = notes
//                )
//                val body = res.body()
//                if (res.isSuccessful && body?.success == true && body.data != null) {
//                    rvGoals.adapter = GoalsAdapter(body.data) { goalId -> deleteGoal(userId, goalId) }
//                    Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), body?.message ?: "Unable to save", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) { Toast.makeText(requireContext(), e.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show() }
//        }
//    }
//
//    private fun loadGoals(userId: Int) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.getGoals(userId)
//                val body = res.body()
//                if (res.isSuccessful && body?.success == true && body.data != null) {
//                    rvGoals.adapter = GoalsAdapter(body.data) { goalId -> deleteGoal(userId, goalId) }
//                } else {
//                    Toast.makeText(requireContext(), body?.message ?: "Unable to load", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) { Toast.makeText(requireContext(), e.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show() }
//        }
//    }
//
//    private fun deleteGoal(userId: Int, goalId: Int) {
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.deleteGoal(userId, goalId)
//                val body = res.body()
//                if (res.isSuccessful && body?.success == true && body.data != null) {
//                    rvGoals.adapter = GoalsAdapter(body.data) { id -> deleteGoal(userId, id) }
//                    Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), body?.message ?: "Unable to delete", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) { Toast.makeText(requireContext(), e.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show() }
//        }
//    }
//}
//
//class GoalsAdapter(
//    private val goals: List<GoalDto>,
//    private val onDelete: (Int) -> Unit
//) : RecyclerView.Adapter<GoalsAdapter.ViewHolder>() {
//
//    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//        val tvTitle: TextView = v.findViewById(R.id.tvGoalTitle)
//        val tvStatus: TextView = v.findViewById(R.id.tvGoalStatus)
//        val btnReset: Button = v.findViewById(R.id.btnGoalReset)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_goal, parent, false))
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val goal = goals[position]
//        holder.tvTitle.text = goal.title
//        holder.tvStatus.text = "${goal.type.uppercase()} � Target ${goal.target_value}"
//        holder.btnReset.text = holder.itemView.context.getString(R.string.delete)
//        holder.btnReset.setOnClickListener { onDelete(goal.id) }
//    }
//
//    override fun getItemCount() = goals.size
//}

package com.example.fitnesstracker.fitness.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.Goal
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch

class GoalsFragment : Fragment() {
    private lateinit var rv: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById(R.id.rvMyGoals)
        rv.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.btnSetGoal).setOnClickListener {
            val title = view.findViewById<EditText>(R.id.etGoalTitle).text.toString()
            val period = view.findViewById<Spinner>(R.id.spinnerPeriod).selectedItem.toString().lowercase()
            if (title.isNotEmpty()) {
                createGoal(title, period)
            }
        }
        loadGoals()
    }

    private fun createGoal(title: String, period: String) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.createGoal(mapOf("title" to title, "period" to period))
                if (res.isSuccessful) loadGoals()
            } catch (e: Exception) {}
        }
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getGoals()
                if (res.isSuccessful && res.body() != null) {
                    rv.adapter = GoalAdapter(res.body()!!) { resetGoal(it) }
                }
            } catch (e: Exception) {}
        }
    }

    private fun resetGoal(id: Int) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.resetGoal(mapOf("id" to id.toString()))
                if (res.isSuccessful) loadGoals()
            } catch (e: Exception) {}
        }
    }

    class GoalAdapter(private val items: List<Goal>, val onReset: (Int) -> Unit) : RecyclerView.Adapter<GoalAdapter.ViewHolder>() {
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tv = v.findViewById<TextView>(android.R.id.text1)
            val btn = v.findViewById<Button>(android.R.id.button1)
        }
        override fun onCreateViewHolder(p: ViewGroup, type: Int) = ViewHolder(LayoutInflater.from(p.context).inflate(R.layout.row_goal, p, false))
        override fun onBindViewHolder(h: ViewHolder, p: Int) {
            h.tv.text = "${items[p].title} (${items[p].status})"
            h.btn.setOnClickListener { onReset(items[p].id) }
        }
        override fun getItemCount() = items.size
    }
}