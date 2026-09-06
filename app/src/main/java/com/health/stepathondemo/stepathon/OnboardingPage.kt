package com.health.stepathondemo.stepathon

data class OnboardingPage(
    val id: Int,
    val backgroundColorsId: List<Int>,
    val iconRes: Int,
    val heading: String,
    val description: String,
)

