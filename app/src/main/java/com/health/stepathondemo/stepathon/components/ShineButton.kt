package com.health.stepathondemo.stepathon.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.dimensionResource
import com.health.stepathondemo.R
import com.health.stepathondemo.ui.theme.ContentDark
import com.health.stepathondemo.ui.theme.OnboardingSurface
import com.health.stepathondemo.ui.theme.ShineStripeWhite

/** Full-width button with a looping shine sweep (lone stripe + a thin/thick pair). */
@Composable
fun ShineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val buttonCorner = dimensionResource(R.dimen.button_corner_radius)
    val stripeWidth = dimensionResource(R.dimen.shine_stripe_width)
    val stripeThickWidth = dimensionResource(R.dimen.shine_thick_stripe_width)
    val stripeSlant = dimensionResource(R.dimen.shine_stripe_slant)
    val stripePairGap = dimensionResource(R.dimen.shine_pair_gap)
    val shineProgress by rememberInfiniteTransition(label = "btnShine").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "btnShineX"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.button_horizontal_margin))
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.button_height))
            .drawBehind {
                // looping shine sweep: lone stripe + a thin/thick pair, drifting right
                val corner = buttonCorner.toPx()
                drawRoundRect(color = OnboardingSurface, cornerRadius = CornerRadius(corner))
                val clip = Path().apply {
                    addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(corner)))
                }
                val thinW = stripeWidth.toPx()
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
                                color = ShineStripeWhite
                            )
                        }
                        stripe(0f, thinW)                                  // lone stripe
                        val thickOffset = groupW - thickW                  // pair right-aligned
                        stripe(thickOffset - pairGap - thinW, thinW)       // thin of the pair
                        stripe(thickOffset, thickW)                        // thick of the pair
                    }
                }
            },
        shape = RoundedCornerShape(buttonCorner),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = ContentDark
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
