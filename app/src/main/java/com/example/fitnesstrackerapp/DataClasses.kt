package com.example.fitnesstrackerapp

data class Workout1(
    val type: String,
    val duration: Int, // duration in minutes
    val caloriesBurned: Int
)

data class FitnessGoal(
    val goalType: String,
    val targetValue: Int, // e.g., target duration in minutes or calories
    var progressValue: Int = 0
)

