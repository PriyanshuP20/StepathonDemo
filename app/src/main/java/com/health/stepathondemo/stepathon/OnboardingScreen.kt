package com.health.stepathondemo.stepathon


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.health.stepathondemo.R
import com.health.stepathondemo.stepathon.components.CurvedText
import com.health.stepathondemo.stepathon.components.OnboardingCopy
import com.health.stepathondemo.stepathon.components.PageDots
import com.health.stepathondemo.stepathon.components.ShineButton
import com.health.stepathondemo.ui.theme.CurvedTitle
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onFinished: () -> Unit = {}
) {
    val index by viewModel.index
    val scope = rememberCoroutineScope()
    val goTo: (Int) -> Unit = { target ->
        scope.launch {
            viewModel.pos.animateTo(
                target.coerceIn(0, viewModel.lastIndex).toFloat(),
                spring(dampingRatio = 0.9f, stiffness = 1200f)
            )
        }
    }

    MascotOnboardingScreen(
        pages = viewModel.pages,
        pos = viewModel.pos,
        index = index,
        onGoTo = goTo,
        onDrag = viewModel::dragBy,
        onFinished = onFinished
    )
}

@Composable
fun MascotOnboardingScreen(
    pages: List<OnboardingPage>,
    pos: Animatable<Float, AnimationVector1D>,
    index: Int,
    onGoTo: (Int) -> Unit,
    onDrag: (Float) -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lastIndex = pages.lastIndex
    val density = LocalDensity.current
    val dragStep = dimensionResource(R.dimen.onboarding_swipe_step)
    val dragPerStep = with(density) { dragStep.toPx() }

    val gradientStart by animateColorAsState(
        colorResource(pages[index].backgroundColorsId.first()),
        tween(500),
        label = "gradientStart"
    )
    val gradientEnd by animateColorAsState(
        colorResource(pages[index].backgroundColorsId.last()),
        tween(500),
        label = "gradientEnd"
    )

    val starRotation = rememberInfiniteTransition(label = "bg_star")
        .animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
            label = "bg_star_angle"
        )

    val starBgAlpha by animateFloatAsState(
        targetValue = if (index >= 1) 1f else 0f,
        animationSpec = tween(300),
        label = "starBgAlpha"
    )
    val sparkleComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.animation_sparkle)
    )

    var stageCenter by remember { mutableStateOf(Offset.Zero) }
    var stageSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val c = if (stageCenter == Offset.Zero) {
                    Offset(size.width / 2f, size.height * 0.45f)
                } else {
                    stageCenter
                }
                val radius = maxOf(
                    hypot(c.x, c.y),
                    hypot(size.width - c.x, c.y),
                    hypot(c.x, size.height - c.y),
                    hypot(size.width - c.x, size.height - c.y)
                )
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(gradientStart, gradientEnd),
                        center = c,
                        radius = radius
                    )
                )
            }

            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    onDrag(-delta / dragPerStep)
                },
                onDragStopped = { velocity ->
                    val v = pos.value

                    val target = when {
                        velocity > 700f  -> ceil(v).toInt() - 1
                        velocity < -700f -> floor(v).toInt() + 1
                        else -> {
                            val frac = v - floor(v)
                            if (frac > 0.4f) ceil(v).toInt() else floor(v).toInt()
                        }
                    }

                    onGoTo(target)
                }
            )
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {

            IconButton(
                onClick = { onGoTo(index - 1) },
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.back_button_padding),
                    top = dimensionResource(R.dimen.back_button_padding)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                    modifier = Modifier.size(dimensionResource(R.dimen.back_icon_size))
                )
            }

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CurvedText(
                    text = stringResource(R.string.app_title),
                    fontSize = CurvedTitle.fontSize,
                    radius = dimensionResource(R.dimen.curved_title_radius),
                    color = Color.White,
                    glowRadius = dimensionResource(R.dimen.curved_title_glow),
                    glowColor = Color.White.copy(alpha = 0.75f)
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned { coords ->
                        stageSize = coords.size
                        stageCenter = coords.positionInRoot() +
                            Offset(coords.size.width / 2f, coords.size.height / 2f)
                    }
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val sparkleSize = dimensionResource(R.dimen.sparkle_size)
                    val sparkleHalf = sparkleSize / 2f
                    val sparkleEdgePadding = dimensionResource(R.dimen.sparkle_edge_padding)
                    Image(
                        painter = painterResource(R.drawable.bg_rotating_star),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .graphicsLayer {
                                alpha = starBgAlpha
                                rotationZ = starRotation.value
                                scaleX = 1.25f
                                scaleY = 1.25f
                            }
                    )

                    LottieAnimation(
                        composition = sparkleComposition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(sparkleSize)

                            .graphicsLayer {
                                val t = pos.value.coerceIn(0f, 1f)
                                alpha = t
                                val k = 1f - t
                                translationX = (stageSize.width / 2f - (sparkleEdgePadding + sparkleHalf).toPx()) * k
                                translationY = (stageSize.height / 2f - sparkleHalf.toPx()) * k
                                scaleX = 0.5f + 0.5f * t
                                scaleY = 0.5f + 0.5f * t
                            }
                    )
                    LottieAnimation(
                        composition = sparkleComposition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(sparkleSize)
                            .graphicsLayer {
                                val t = pos.value.coerceIn(0f, 1f)
                                alpha = t
                                val k = 1f - t
                                translationX = ((sparkleEdgePadding + sparkleHalf).toPx() - stageSize.width / 2f) * k
                                translationY = (sparkleHalf.toPx() - stageSize.height / 2f) * k
                                scaleX = 0.5f + 0.5f * t
                                scaleY = 0.5f + 0.5f * t
                            }
                    )
                }
                IconStage(
                    pages = pages,
                    pos = pos,
                    smallSize = dimensionResource(R.dimen.corner_preview_size),
                    modifier = Modifier.fillMaxSize(),
                    cornerPadding = 0.dp
                )
            }

            AnimatedContent(
                targetState = index,
                modifier = Modifier.fillMaxWidth(),
                transitionSpec = {
                    fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                },
                label = "copy"
            ) { i ->
                OnboardingCopy(page = pages[i])
            }

            PageDots(
                pageCount = pages.size,
                currentPage = index,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            ShineButton(
                text = stringResource(if (index == lastIndex) R.string.onboarding_lets_go else R.string.onboarding_next),
                onClick = { if (index == lastIndex) onFinished() else onGoTo(index + 1) }
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.content_bottom_spacing)))
        }
    }
}

@Composable
private fun IconStage(
    pages: List<OnboardingPage>,
    pos: Animatable<Float, AnimationVector1D>,
    modifier: Modifier = Modifier,
    smallSize: Dp = 72.dp,
    cornerPadding: Dp = 24.dp,
) {
    val density = LocalDensity.current
    val smallPx = with(density) { smallSize.toPx() }
    val padPx = with(density) { cornerPadding.toPx() }

    Layout(
        content = {
            pages.forEach { page ->
                Image(painterResource(page.iconRes), contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        },
        modifier = modifier
    ) { measurables, constraints ->

        val big = minOf(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val placeables = measurables.map { it.measure(Constraints.fixed(big.roundToInt(), big.roundToInt())) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val w = constraints.maxWidth.toFloat()
            val h = constraints.maxHeight.toFloat()
            val center = Offset(w / 2f, h * 0.5f)
            val corner = Offset(w - padPx - smallPx / 2f, padPx + smallPx / 2f)

            val p = pos.value

            pages.indices.forEach { j ->
                val t = (p - j + 1f).coerceIn(0f, 1f)
                val behind = (p - j).coerceIn(0f, 1f)
                val a = (p - j + 2f).coerceIn(0f, 1f) * (1f - behind)
                if (a <= 0f) return@forEach

                val x = lerp(corner.x, center.x, t)
                val y = lerp(corner.y, center.y, t)
                val scale = lerp(smallPx, big, t) / big * (1f - 0.06f * behind)

                placeables[j].placeWithLayer(
                    IntOffset((x - big / 2f).roundToInt(), (y - big / 2f).roundToInt())
                ) {
                    scaleX = scale
                    scaleY = scale
                    alpha = a
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
            }
        }
    }
}
