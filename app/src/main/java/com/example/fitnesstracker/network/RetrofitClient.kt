//package com.example.fitnesstracker.network
//
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitClient {
//
//    // Matches the PHP server endpoint e.g. http://192.168.1.13:8001/server/
//    private const val BASE_URL = "http://10.0.2.2/server/"
//
//    // Gson instance for JSON parsing
//    private val gson: Gson = GsonBuilder().create()
//
//    // Logging interceptor for debugging network requests
//    private val logging = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    // OkHttp client with logging
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .build()
//
//    // Retrofit instance
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(client) // attach OkHttp client with logging
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//    }
//
//    // API service instance
//    val instance: ApiService by lazy { retrofit.create(ApiService::class.java) }
//}
package com.example.fitnesstracker.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
//    private const val BASE_URL = "http://192.168.1.17/fitness/server/"
    private const val BASE_URL = "http://10.0.2.2/fitness/server/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // 1. Initialize SessionManager here as well
    fun init(context: Context) {
        SessionManager.init(context)
    }

    // 2. Cookie Handling Interceptor
    private val cookieInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // --- GET SAVED COOKIE ---
        val savedCookie = SessionManager.getCookie()

        val requestBuilder = originalRequest.newBuilder()
        if (savedCookie != null) {
            requestBuilder.header("Cookie", savedCookie)
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // --- SAVE NEW COOKIE (If server sends one) ---
        val setCookieHeader = response.header("Set-Cookie")
        if (setCookieHeader != null) {
            SessionManager.saveCookie(setCookieHeader)
        }

        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(cookieInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}