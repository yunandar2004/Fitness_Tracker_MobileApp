//package com.example.fitnesstracker.model
//
///**
// * Matches the PHP helpers in app/src/main/server.
// */
//data class ApiResponse<T>(
//    val success: Boolean,
//    val message: String,
//    val data: T?
//)
//
//data class User( val id: Int, val name: String, val email: String, val date_of_birth: String, val current_weight_kg: Double, val gender: String )
//
//data class AuthPayload(
//    val token: String,
//    val user: User
//)
//
//data class GoalDto(
//    val id: Int,
//    val user_id: Int,
//    val type: String,
//    val target_value: Double,
//    val current_value: Double,
//    val deadline: String?,
//    val title: String,
//    val notes: String?
//)
//
//data class WorkoutDto(
//    val id: Int,
//    val user_id: Int,
//    val activity: String,
//    val duration_minutes: Int,
//    val distance_km: Double?,
//    val calories: Int,
//    val notes: String?,
//    val location: String?,
//    val recorded_at: String
//)
//
//data class ProgressSummary(
//    val total_workouts: Int,
//    val total_minutes: Int,
//    val distance_km: Double,
//    val calories: Int
//)

package com.example.fitnesstracker.model

data class DefaultResponse(val message: String)
data class LoginResponse(val message: String, val role: String, val dashboard: String)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val date_of_birth: String,
    val current_weight_kg: Double,
    val gender: String,
    val role: String,
    val profile_image: String
)

data class Goal(
    val id: Int,
    val title: String,
    val period: String,
    val status: String,
    val created_at: String
)

data class Workout(
    val id: Int,
    val activity: String,
    val burned_calories: Double,
    val time_minutes: Int,
    val created_at: String
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