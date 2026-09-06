package com.health.stepathondemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.health.stepathondemo.stepathon.OnboardingScreen
import com.health.stepathondemo.ui.theme.StepathonDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepathonDemoTheme {
                OnboardingScreen()
            }
        }
    }
}
