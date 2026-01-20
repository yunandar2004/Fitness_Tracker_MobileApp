package com.example.fitnesstracker.model

class AdminUser(name: String, email: String) : User(name, email) {
    override fun getRole() = "admin"

    fun deleteUser(user: User) {
        // Admin-only logic to delete a user
    }
}
