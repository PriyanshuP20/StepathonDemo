package com.health.stepathondemo.stepathon

import com.health.stepathondemo.R

val onboardingPages = listOf(
    OnboardingPage(
        id = 1,
        backgroundColorsId = listOf(R.color.onboarding_purple_start, R.color.onboarding_purple_end),
        iconRes = R.drawable.ic_walking_robo,
        headingRes = R.string.onboarding_heading_1,
        descriptionRes = R.string.onboarding_description_1
    ),

    OnboardingPage(
        id = 2,
        backgroundColorsId = listOf(R.color.onboarding_teal_start,R.color.onboarding_blue_end),
        iconRes = R.drawable.ic_trophy,
        headingRes = R.string.onboarding_heading_2,
        descriptionRes = R.string.onboarding_description_2
    ),

    OnboardingPage(
        id = 3,
        backgroundColorsId = listOf(R.color.onboarding_orange_start,R.color.onboarding_pink_end),
        iconRes = R.drawable.ic_gift_box,
        headingRes = R.string.onboarding_heading_3,
        descriptionRes = R.string.onboarding_description_3
    )
)
