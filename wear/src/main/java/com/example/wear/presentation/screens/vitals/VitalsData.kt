package com.example.wear.presentation.screens.vitals

// Data classes for vitals
data class HeartRateData(
    val bpm: Int,
    val graphPoints: List<Float> = listOf(0.4f, 0.3f, 0.5f, 0.2f, 0.6f, 0.4f, 0.3f)
)

data class BloodPressureData(
    val systolic: Int,
    val diastolic: Int,
    val systolicPoints: List<Float> = listOf(0.3f, 0.2f, 0.4f, 0.3f, 0.5f, 0.4f),
    val diastolicPoints: List<Float> = listOf(0.7f, 0.6f, 0.8f, 0.7f, 0.9f, 0.8f)
)

data class BloodGlucoseData(
    val level: Int,
    val graphPoints: List<Float> = listOf(0.6f, 0.5f, 0.7f, 0.4f, 0.8f, 0.6f, 0.5f)
)