
package com.example.fitnesstracker.fitness.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.AppUser
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        try { RetrofitClient.init(this) } catch (e: Exception) {}

        val rv = findViewById<RecyclerView>(R.id.rvAdminUsers)
        rv.layoutManager = LinearLayoutManager(this)
        loadUsers(rv)
    }

    private fun loadUsers(rv: RecyclerView) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getAllUsers()
                if (res.isSuccessful && res.body() != null) {
                    rv.adapter = AdminAdapter(res.body()!!)
                }
            } catch (e: Exception) { Toast.makeText(this@AdminActivity, "Error", Toast.LENGTH_SHORT).show() }
        }
    }

    inner class AdminAdapter(private val users: List<AppUser>) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvName = v.findViewById<TextView>(R.id.tvAdminName)
            val tvEmail = v.findViewById<TextView>(R.id.tvAdminEmail)
            val btnDel = v.findViewById<Button>(R.id.btnAdminDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_admin_user, parent, false))
        }
        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            val u = users[pos]
            holder.tvName.text = "${u.name} (${u.role})"
            holder.tvEmail.text = u.email
            holder.btnDel.setOnClickListener { deleteUser(u.id) }
        }
        override fun getItemCount() = users.size
    }

    private fun deleteUser(id: Int) {
        lifecycleScope.launch {
            try {
                // Fixed: ID to String
                val res = RetrofitClient.instance.adminDeleteUser(mapOf("user_id" to id.toString()))
                if (res.isSuccessful) {
                    Toast.makeText(this@AdminActivity, "Deleted", Toast.LENGTH_SHORT).show()
                    loadUsers(findViewById(R.id.rvAdminUsers))
                }
            } catch (e: Exception) {}
        }
    }
}