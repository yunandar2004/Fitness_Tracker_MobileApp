//
//package com.example.fitnesstracker.network
//import android.content.Context
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//import com.example.fitnesstracker.network.RetrofitClient
//
//
//object RetrofitClient {
//
//    const val BASE_URL = "http://10.0.2.2/fitness/server/"
//
//    val instance: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
//
//    fun init(context: Context) {
//        SessionManager.init(context)
//    }
//
//    private val jsonInterceptor = Interceptor { chain ->
//        val request = chain.request().newBuilder()
//            .addHeader("Content-Type", "application/json")
//            .addHeader("Accept", "application/json")
//            .build()
//        chain.proceed(request)
//    }
//
//    private val cookieInterceptor = Interceptor { chain ->
//        val originalRequest = chain.request()
//
//        val savedCookie = SessionManager.getCookie()
//
//        val requestBuilder = originalRequest.newBuilder()
//        if (savedCookie != null) {
//            requestBuilder.header("Cookie", savedCookie)
//        }
//
//        val request = requestBuilder.build()
//        val response = chain.proceed(request)
//
//        val setCookieHeader = response.header("Set-Cookie")
//        if (setCookieHeader != null) {
//            SessionManager.saveCookie(setCookieHeader)
//        }
//
//        response
//    }
//
//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(jsonInterceptor)
//        .addInterceptor(cookieInterceptor)
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .build()
//}
package com.example.fitnesstracker.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.MultipartBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    const val BASE_URL = "http://10.0.2.2/fitness/server/"

    fun init(context: Context) {
        SessionManager.init(context)
    }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Adds JSON headers only when needed
    private val jsonInterceptor = Interceptor { chain ->
        val original = chain.request()

        val builder = original.newBuilder()
            .addHeader("Accept", "application/json")

        if (original.body !is MultipartBody) {
            builder.addHeader("Content-Type", "application/json")
        }

        chain.proceed(builder.build())
    }

    // Handles PHP session cookies
    private val cookieInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        SessionManager.getCookie()?.let {
            builder.header("Cookie", it)
        }

        val response = chain.proceed(builder.build())

        response.header("Set-Cookie")?.let {
            SessionManager.saveCookie(it)
        }

        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(jsonInterceptor)
        .addInterceptor(cookieInterceptor)
        .addInterceptor(loggingInterceptor) // optional but helpful
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}
