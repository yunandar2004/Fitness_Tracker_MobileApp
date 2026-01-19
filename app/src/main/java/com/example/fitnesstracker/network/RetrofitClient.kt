
package com.example.fitnesstracker.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.fitnesstracker.network.RetrofitClient

object RetrofitClient {
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