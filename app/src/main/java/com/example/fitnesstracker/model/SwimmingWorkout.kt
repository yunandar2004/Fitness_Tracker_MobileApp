package com.example.fitnesstracker.model

class SwimmingWorkout(
    timeMinutes: Int,
    userWeight: Double,
    val stroke: String
) : Workout("Swimming", timeMinutes, userWeight) {
    override fun calculateCalories(): Double = when(stroke) {
        "freestyle" -> 8.0 * userWeight * (timeMinutes / 60.0)
        "butterfly" -> 13.8 * userWeight * (timeMinutes / 60.0)
        "breaststroke" -> 10.0 * userWeight * (timeMinutes / 60.0)
        else -> 0.0
    }
}
