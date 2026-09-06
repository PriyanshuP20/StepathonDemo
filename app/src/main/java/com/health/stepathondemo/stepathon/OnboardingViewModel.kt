package com.health.stepathondemo.stepathon

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OnboardingViewModel : ViewModel() {

    val pages = onboardingPages
    val lastIndex = pages.lastIndex

    private val _pos = Animatable(0f)

    val pos: Animatable<Float, AnimationVector1D> = _pos

    val index: State<Int> = derivedStateOf { _pos.value.roundToInt().coerceIn(0, lastIndex) }

    fun dragBy(steps: Float) {
        viewModelScope.launch {
            _pos.snapTo((_pos.value + steps).coerceIn(0f, lastIndex.toFloat()))
        }
    }
}
