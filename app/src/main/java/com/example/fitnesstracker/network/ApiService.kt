//package com.example.fitnesstracker.network
//
//import com.example.fitnesstracker.model.*
//import retrofit2.Response
//import retrofit2.http.*
//
//data class DefaultResponse(val message: String)
//
//interface ApiService {
//    // CHECK THIS SECTION
//    @POST("auth.php?action=register") // 1. Ensure "action=register" matches PHP
//    suspend fun register(@Body body: Map<String, String>): Response<DefaultResponse>
//    @FormUrlEncoded
//    @POST("login.php")
//    suspend fun login(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Response<ApiResponse<AuthPayload>>
//
//    @FormUrlEncoded
//    @POST("register.php")
//    suspend fun register(
//        @Field("name") name: String,
//        @Field("email") email: String,
//        @Field("password") password: String,
//        @Field("date_of_birth") dob: String,
//        @Field("current_weight_kg") weight: String?,
//        @Field("gender") gender: String?
//    ): Response<ApiResponse<AuthPayload>>
//
//    @GET("goals.php")
//    suspend fun getGoals(
//        @Query("user_id") userId: Int
//    ): Response<ApiResponse<List<GoalDto>>>
//
//    @FormUrlEncoded
//    @POST("goals.php")
//    suspend fun saveGoal(
//        @Field("user_id") userId: Int,
//        @Field("type") type: String,
//        @Field("target_value") targetValue: String,
//        @Field("deadline") deadline: String,
//        @Field("title") title: String,
//        @Field("notes") notes: String? = null,
//        @Field("goal_id") goalId: String? = null
//    ): Response<ApiResponse<List<GoalDto>>>
//
//    @FormUrlEncoded
//    @POST("delete_goal.php")
//    suspend fun deleteGoal(
//        @Field("user_id") userId: Int,
//        @Field("goal_id") goalId: Int
//    ): Response<ApiResponse<List<GoalDto>>>
//
//    @GET("workouts.php")
//    suspend fun getWorkouts(
//        @Query("user_id") userId: Int
//    ): Response<ApiResponse<List<WorkoutDto>>>
//
//    @FormUrlEncoded
//    @POST("workouts.php")
//    suspend fun logWorkout(
//        @Field("user_id") userId: Int,
//        @Field("activity") activity: String,
//        @Field("duration_minutes") durationMinutes: Int,
//        @Field("calories") calories: Int,
//        @Field("distance_km") distanceKm: String? = null,
//        @Field("notes") notes: String? = null,
//        @Field("location") location: String? = null
//    ): Response<ApiResponse<WorkoutDto>>
//
//    @GET("progress.php")
//    suspend fun getProgress(
//        @Query("user_id") userId: Int
//    ): Response<ApiResponse<ProgressSummary>>
//}
//package com.example.fitnesstracker.network

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
    suspend fun getProfile(): Response<User>

    @PUT("users.php?action=update")
    suspend fun updateProfile(@Body body: Map<String, String>): Response<DefaultResponse>

    @PUT("users.php?action=password")
    suspend fun changePassword(@Body body: Map<String, String>): Response<DefaultResponse>

    @HTTP(method = "DELETE", path = "users.php?action=delete", hasBody = true)
    suspend fun deleteAccount(@Body body: Map<String, String>): Response<DefaultResponse>
    // pr0file
//    @Multipart
//    @POST("users.php?action=upload_image")
//    suspend fun uploadProfileImage(
//        @Part image: MultipartBody.Part
//    ): Response<ImageResponse>

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

    @HTTP(method = "DELETE", path = "goals.php?action=delete", hasBody = true)
    suspend fun deleteGoal(@Body body: Map<String, String>): Response<DefaultResponse>



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
    suspend fun getAllUsers(): Response<List<User>>

    @PUT("admin.php?action=role")
    suspend fun updateUserRole(@Body body: Map<String, String>): Response<DefaultResponse>

    @HTTP(method = "DELETE", path = "admin.php?action=delete_user", hasBody = true)
    suspend fun adminDeleteUser(@Body body: Map<String, String>): Response<DefaultResponse>

}