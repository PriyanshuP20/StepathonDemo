package com.health.stepathondemo.stepathon.components

import android.graphics.Typeface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import androidx.core.graphics.withRotation

@Composable
fun CurvedText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 40.sp,
    letterSpacing: TextUnit = 2.sp,
    color: Color = Color.White,
    fontFamily: FontFamily = FontFamily.Default,
    fontWeight: FontWeight = FontWeight.Bold,
    radius: Dp = 250.dp,
    arcUp: Boolean = true,
    typeface: Typeface? = null,
    glowRadius: Dp = 0.dp,
    glowColor: Color = color,
) {
    val density = LocalDensity.current
    val fontSizePx = with(density) { fontSize.toPx() }
    val spacingPx = with(density) { letterSpacing.toPx() }
    val radiusPx = with(density) { radius.toPx() }
    val glowRadiusPx = with(density) { glowRadius.toPx() }

    val paint = remember(fontSizePx, fontFamily, fontWeight, color, typeface, glowRadiusPx, glowColor) {
        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            textSize = fontSizePx
            textAlign = android.graphics.Paint.Align.CENTER
            this.color = color.toArgb()
            this.typeface = typeface ?: run {
                val base = when (fontFamily) {
                    FontFamily.Serif -> Typeface.SERIF
                    FontFamily.Monospace -> Typeface.MONOSPACE
                    FontFamily.Cursive -> Typeface.create("cursive", Typeface.NORMAL)
                    else -> Typeface.SANS_SERIF
                }
                Typeface.create(
                    base,
                    if (fontWeight >= FontWeight.Bold) Typeface.BOLD else Typeface.NORMAL
                )
            }
            if (glowRadiusPx > 0f) {
                setShadowLayer(glowRadiusPx, 0f, 0f, glowColor.toArgb())
            }
        }
    }

    val metrics = remember(text, paint, radiusPx, spacingPx, arcUp) {
        val charWidths = FloatArray(text.length) { paint.measureText(text[it].toString()) }
        val chars = Array(text.length) { text[it].toString() }
        val arcLength = if (text.isEmpty()) 0f
        else charWidths.sum() + spacingPx * (text.length - 1)
        val halfSpan = if (radiusPx > 0f && text.isNotEmpty()) arcLength / (2f * radiusPx) else 0f

        val fm = paint.fontMetrics
        val ascent = -fm.ascent
        val descent = fm.descent

        val sinH = sin(halfSpan)
        val cosH = cos(halfSpan)
        val endHalf = if (text.isEmpty()) 0f else max(charWidths.first(), charWidths.last()) / 2f

        val width = ceil(2f * radiusPx * sinH + 2f * (endHalf * cosH + ascent * sinH)).toInt() + 2
        val poke = max(0f, endHalf * sinH - (radiusPx + min(ascent, descent)) * (1f - cosH))
        val height =
            ceil(radiusPx * (1f - cosH) + ascent + descent + endHalf * sinH + poke).toInt() + 2

        ArcLayout(
            chars = chars,
            charWidths = charWidths,
            width = width,
            height = height,
            cy = if (arcUp) radiusPx + ascent + poke else height - radiusPx - descent - poke,
            startAngle = if (arcUp) -PI.toFloat() / 2f - halfSpan else PI.toFloat() / 2f + halfSpan,
            direction = if (arcUp) 1f else -1f,
        )
    }

    Layout(
        content = {},
        modifier = modifier.drawBehind {
            if (text.isEmpty()) return@drawBehind
            val canvas = drawContext.canvas.nativeCanvas
            val cx = size.width / 2f
            val cy = metrics.cy + (size.height - metrics.height) / 2f

            var angle = metrics.startAngle
            for (i in text.indices) {
                val w = metrics.charWidths[i]
                val charAngle = angle + metrics.direction * w / (2f * radiusPx)
                val x = cx + radiusPx * cos(charAngle)
                val y = cy + radiusPx * sin(charAngle)

                val rotation = Math.toDegrees(
                    (charAngle + (if (arcUp) PI / 2 else -PI / 2))
                ).toFloat()

                canvas.withRotation(rotation, x, y) {
                    drawText(metrics.chars[i], x, y, paint)
                }

                angle += metrics.direction * (w + spacingPx) / radiusPx
            }
        }
    ) { _, constraints ->
        val w = metrics.width.coerceIn(constraints.minWidth, constraints.maxWidth)
        val h = metrics.height.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(w, h) {}
    }
}

private class ArcLayout(
    val chars: Array<String>,
    val charWidths: FloatArray,
    val width: Int,
    val height: Int,
    val cy: Float,
    val startAngle: Float,
    val direction: Float,
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 400, heightDp = 200)
@Composable
private fun CurvedTextPreview() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CurvedText(
            text = "Step-a-thon",
            fontSize = 44.sp,
            radius = 170.dp,
            color = Color.White,
        )
    }
}