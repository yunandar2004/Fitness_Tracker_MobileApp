package com.example.fitnesstracker.model

public open  class Workout(
    val activity: String,
    val timeMinutes: Int,
    val userWeight: Double
) {
    open fun calculateCalories(): Double = 0.0
}
