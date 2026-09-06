package com.health.stepathondemo.stepathon

import androidx.annotation.StringRes

data class OnboardingPage(
    val id: Int,
    val backgroundColorsId: List<Int>,
    val iconRes: Int,
    @StringRes val headingRes: Int,
    @StringRes val descriptionRes: Int,
)
