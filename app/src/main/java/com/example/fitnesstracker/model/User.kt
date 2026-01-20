package com.example.fitnesstracker.model

open class User(
    val name: String,
    val email: String
) {
    open fun getRole() = "user"
}
