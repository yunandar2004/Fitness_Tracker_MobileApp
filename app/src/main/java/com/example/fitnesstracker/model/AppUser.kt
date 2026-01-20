package com.example.fitnesstracker.model

class AppUser(private var password: String) {
    fun setPassword(newPassword: String) {
        if (newPassword.length >= 8) password = newPassword
    }
    fun checkPassword(input: String) = input == password
}
