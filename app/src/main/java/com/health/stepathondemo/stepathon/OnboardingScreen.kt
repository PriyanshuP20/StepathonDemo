package com.health.stepathondemo.stepathon


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.health.stepathondemo.ui.theme.CurvedTitle
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun OnboardingScreen(
    innerPadding: PaddingValues,
    viewModel: OnboardingViewModel = viewModel()
) {
    MascotOnboardingScreen(pages = viewModel.pages)
}

@Composable
fun getColors(colorResIds: List<Int>): List<Color> {
    return colorResIds.map { colorResource(it) }
}


@Composable
fun MascotOnboardingScreen(
    pages: List<OnboardingPage>,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val lastIndex = pages.lastIndex

    val pos = remember { Animatable(0f) }
    val index by remember { derivedStateOf { pos.value.roundToInt().coerceIn(0, lastIndex) } }

    val dragStep = dimensionResource(R.dimen.onboarding_swipe_step)
    val dragPerStep = with(density) { dragStep.toPx() }

    val goTo: (Int) -> Unit = { target ->
        scope.launch {
            pos.animateTo(
                target.coerceIn(0, lastIndex).toFloat(),
                spring(dampingRatio = 0.9f, stiffness = 1200f)
            )
        }
    }

    val pageColors = getColors(pages[index].backgroundColorsId)
    val gradientStart by animateColorAsState(pageColors.first(), tween(500), label = "gradientStart")
    val gradientEnd by animateColorAsState(pageColors.last(), tween(500), label = "gradientEnd")

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

    val shineProgress by rememberInfiniteTransition(label = "btnShine").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "btnShineX"
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
                    scope.launch {
                        pos.snapTo(
                            (pos.value - delta / dragPerStep)
                                .coerceIn(0f, lastIndex.toFloat())
                        )
                    }
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

                    goTo(target)
                }
            )
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {

            IconButton(
                onClick = { goTo(index - 1) },
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
                    val sparkleInnerPadding = dimensionResource(R.dimen.sparkle_inner_padding)
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
                            .padding(sparkleInnerPadding)
                            .padding(start = sparkleEdgePadding)
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
                            .padding(sparkleInnerPadding)
                            .padding(end = sparkleEdgePadding)
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
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(pages[i].headingRes), color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(dimensionResource(R.dimen.heading_bottom_spacing)))
                    Text(
                        stringResource(pages[i].descriptionRes),
                        color = Color.White.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.description_horizontal_padding))
                    )
                }
            }

            Row(
                Modifier.align(Alignment.CenterHorizontally).padding(vertical = dimensionResource(R.dimen.dots_vertical_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dots_spacing))
            ) {
                pages.indices.forEach { i ->
                    val w by animateDpAsState(
                        if (i == index) dimensionResource(R.dimen.dot_size_selected) else dimensionResource(R.dimen.dot_size),
                        label = "dotW"
                    )
                    Box(
                        Modifier
                            .size(w, dimensionResource(R.dimen.dot_size))
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (i == index) 0.95f else 0.35f))
                    )
                }
            }

            val buttonCorner = dimensionResource(R.dimen.button_corner_radius)
            val stripeWidth = dimensionResource(R.dimen.shine_stripe_width)
            val stripeThickWidth = dimensionResource(R.dimen.shine_thick_stripe_width)
            val stripeSlant = dimensionResource(R.dimen.shine_stripe_slant)
            val stripePairGap = dimensionResource(R.dimen.shine_pair_gap)

            Button(
                onClick = { if (index == lastIndex) onFinished() else goTo(index + 1) },
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.button_horizontal_margin))
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height))
                    .drawBehind {

                        val corner = buttonCorner.toPx()
                        drawRoundRect(color = Color(0xFFF4F2F8), cornerRadius = CornerRadius(corner))
                        val clip = Path().apply {
                            addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(corner)))
                        }
                        val stripeW = stripeWidth.toPx()
                        val thickW = stripeThickWidth.toPx()
                        val slant = stripeSlant.toPx()
                        val pairGap = stripePairGap.toPx()

                        val groupW = size.width * 0.72f
                        val groupSpan = groupW + slant

                        val x = -groupSpan + (size.width + groupSpan) * shineProgress
                        val top = -slant
                        val bottom = size.height + slant

                        clipPath(clip) {
                            translate(x) {
                                fun stripe(offset: Float, width: Float) {
                                    drawPath(
                                        path = Path().apply {
                                            moveTo(offset, bottom)
                                            lineTo(offset + width, bottom)
                                            lineTo(offset + width + slant, top)
                                            lineTo(offset + slant, top)
                                            close()
                                        },
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                                stripe(0f, stripeW)
                                val thickOffset = groupW - thickW
                                stripe(thickOffset - pairGap - stripeW, stripeW)
                                stripe(thickOffset, thickW)
                            }
                        }
                    },
                shape = RoundedCornerShape(buttonCorner),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF171041)
                )
            ) {
                Text(
                    stringResource(if (index == lastIndex) R.string.onboarding_lets_go else R.string.onboarding_next),
                    style = MaterialTheme.typography.labelLarge
                )
            }
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
        val placeable = measurables.map { it.measure(Constraints.fixed(big.roundToInt(), big.roundToInt())) }

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

                placeable[j].placeWithLayer(
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