
package com.example.fitnesstracker.model
data class DefaultResponse(val message: String)
data class LoginResponse(val message: String, val role: String, val dashboard: String)
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val date_of_birth: String,
    val current_weight_kg: Float,
    val gender: String,
    val role: String,
    val profile_image: String? // <- nullable
)

data class ImageResponse(
    val message: String,
    val image: String
)
data class Goal(
    val id: Int,
    val title: String,
    val period: String,
    val start_date: String?,
    val end_date: String?,
    val status: String,
    val current_value: Int? = 0,  // track progress
    val target_value: Int? = 0    // target
)

data class Workout(
    val id: Int,
    val activity: String,
    val burned_calories: Double,
    val time_minutes: Int,
    val created_at: String? = null
)
data class WorkoutResponse(
    val message: String,
    val burned_calories: Double,
    val challenge: String?
)

data class CalculateResponse(
    val burned_calories: Double,
    val max_heart_rate: Int,
    val heart_rate_used: Int
)
