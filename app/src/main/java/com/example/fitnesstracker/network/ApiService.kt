package com.example.fitnesstracker.network
import com.example.fitnesstracker.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("auth.php?action=register")
    suspend fun register(@Body body: Map<String, String>): Response<DefaultResponse>
    @POST("auth.php?action=login")
    suspend fun login(@Body body: Map<String, String>): Response<LoginResponse>
    @POST("auth.php?action=logout")
    suspend fun logout(): Response<DefaultResponse>

    // User
    @GET("users.php?action=me")
    suspend fun getProfile(): Response<AppUser>
    @PUT("users.php?action=update")
    suspend fun updateProfile(@Body body: Map<String, String>): Response<DefaultResponse>
    @PUT("users.php?action=password")
    suspend fun changePassword(@Body body: Map<String, String>): Response<DefaultResponse>
    @HTTP(method = "DELETE", path = "users.php?action=delete", hasBody = true)
    suspend fun deleteAccount(@Body body: Map<String, String>): Response<DefaultResponse>
    @Multipart
    @POST("profile/update_image")
    suspend fun uploadProfileImage(
        @Part profile_image: MultipartBody.Part
    ): Response<Any>

    // Goals
    @POST("goals.php?action=create")
    suspend fun createGoal(
        @Body body: Map<String, String>
    ): Response<DefaultResponse>
    @GET("goals.php?action=list")
    suspend fun getGoals(): Response<List<Goal>>
    @POST("goals.php?action=reset")
    suspend fun resetGoal(
        @Body body: Map<String, String>
    ): Response<DefaultResponse>
    @PUT("goals.php?action=update")
    suspend fun updateGoal(@Body body: Map<String, String>): Response<DefaultResponse>

//    @HTTP(method = "DELETE", path = "goals.php?action=delete", hasBody = true)
//    suspend fun deleteGoal(@Body body: Map<String, String>): Response<DefaultResponse>
@HTTP(
    method = "DELETE",
    path = "goals.php?action=delete",
    hasBody = true
)
suspend fun deleteGoal(
    @Body body: Map<String, String>
): Response<DefaultResponse>

    // Workouts
    @POST("workouts.php?action=create")
    suspend fun createWorkout(@Body body: Map<String, String>): Response<WorkoutResponse>
    @GET("workouts.php?action=history")
    suspend fun getHistory(): Response<List<Workout>>

    // Calculate
    @POST("calculate.php")
    suspend fun calculateCalories(@Body body: Map<String, String>): Response<CalculateResponse>

    // Admin
    @GET("admin.php?action=users")
    suspend fun getAllUsers(): Response<List<AppUser>>

    @PUT("admin.php?action=role")
    suspend fun updateUserRole(@Body body: Map<String, String>): Response<DefaultResponse>

    @HTTP(method = "DELETE", path = "admin.php?action=delete_user", hasBody = true)
    suspend fun adminDeleteUser(@Body body: Map<String, String>): Response<DefaultResponse>

}