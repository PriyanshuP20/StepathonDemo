package com.health.stepathondemo.stepathon.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.health.stepathondemo.R

/** Page position indicator dots. */
@Composable
fun PageDots(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = dimensionResource(R.dimen.dots_vertical_padding)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dots_spacing))
    ) {
        repeat(pageCount) { i ->
            val width by animateDpAsState(
                if (i == currentPage) dimensionResource(R.dimen.dot_size_selected) else dimensionResource(R.dimen.dot_size),
                label = "dotW"
            )
            Box(
                Modifier
                    .size(width, dimensionResource(R.dimen.dot_size))
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = if (i == currentPage) 0.95f else 0.35f))
            )
        }
    }
}
