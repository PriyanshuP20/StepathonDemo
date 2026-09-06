package com.health.stepathondemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BrandPurple = Color(0xFF5335D3)
val BrandPurpleLight = Color(0xFF7C5CFF)

private val LightColors = lightColorScheme(primary = BrandPurple)
private val DarkColors = darkColorScheme(primary = BrandPurpleLight)

@Composable
fun StepathonDemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
