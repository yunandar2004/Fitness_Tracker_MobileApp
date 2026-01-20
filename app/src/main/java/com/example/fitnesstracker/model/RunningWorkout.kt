package com.example.fitnesstracker.model

class RunningWorkout(
    timeMinutes: Int,
    userWeight: Double,
    val speed: Double
) : Workout("Running", timeMinutes, userWeight) {
    override fun calculateCalories(): Double = speed * userWeight * (timeMinutes / 60.0)
}
