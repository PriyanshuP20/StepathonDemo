package com.health.stepathondemo.stepathon

import com.health.stepathondemo.R

val onboardingPages = listOf(
    OnboardingPage(
        id = 1,
        backgroundColorsId = listOf(R.color.onboarding_purple_start, R.color.onboarding_purple_end),
        iconRes = R.drawable.ic_walking_robo,
        heading = "Step Up and Score",
        description = "Join the Race, Lace Up, and \nEmbrace Health!"
    ),

    OnboardingPage(
        id = 2,
        backgroundColorsId = listOf(R.color.onboarding_teal_start,R.color.onboarding_blue_end),
        iconRes = R.drawable.ic_trophy,
        heading = "Claim the Throne",
        description = "Compete with your colleagues for Top \nRanks"
    ),

    OnboardingPage(
        id = 3,
        backgroundColorsId = listOf(R.color.onboarding_orange_start,R.color.onboarding_pink_end),
        iconRes = R.drawable.ic_gift_box,
        heading = "Score Big!!",
        description = "Victory Unlocks Spectacular Vouchers, \nCashbacks, and Beyond"
    )
)

