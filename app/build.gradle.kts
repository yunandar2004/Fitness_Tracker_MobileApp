import java.io.FileInputStream
import java.util.Properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if(localPropertiesFile.exists()){
    localProperties.load(FileInputStream(localPropertiesFile))
}
val mapsApiKey = localProperties.getProperty("MAPS_API_KEY")?.replace("\"", "") ?: ""


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    id("org.jetbrains.kotlin.plugin.compose") version "1.9.0" // adjust if neede

}

android {
    namespace = "com.example.fitnesstracker"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.fitnesstracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    // --- KEEP THESE (Catalog versions) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material)
    implementation(libs.play.services.location)
//    implementation("com.github.bumptech.glide:glide:4.15.1")
//    kapt("com.github.bumptech.glide:compiler:4.15.1")
//    implementation("com.github.bumptech.glide:glide:4.16.0")
//    implementation("com.github.bumptech.glide:compiler:4.16.0")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0") // For displaying Google Maps and markers
    implementation("com.google.android.gms:play-services-location:21.2.0")// For GPS and geolocation features
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.0") // for displaying activity logs
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // REST API calls
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON serialization and deserialization
    implementation("com.squareup.okhttp3:okhttp:4.10.0")// HTTP client for network requests
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0") // Logs network requests for debugging
    implementation("com.google.code.gson:gson:2.10.1") // JSON parsing library
    implementation("androidx.appcompat:appcompat:1.7.1") // Provides backward-compatible app features
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")// For flexible UI layout design
    implementation("com.github.bumptech.glide:glide:4.15.1")// Image loading and caching library
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")// Asynchronous tasks using Kotlin coroutines
    implementation(libs.androidx.activity)
    implementation(libs.play.services.maps) // Downgraded from 1.10.2

    // --- TESTS ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}