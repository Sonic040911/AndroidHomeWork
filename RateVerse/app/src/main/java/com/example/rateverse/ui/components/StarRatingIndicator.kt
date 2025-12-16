package com.example.rateverse.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingIndicator(
    score: Double,
    modifier: Modifier = Modifier,
    onRatingClick: ((Int) -> Unit)? = null
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starLevel = index + 1

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star $starLevel",
                tint = when {
                    starLevel <= score ->
                        MaterialTheme.colorScheme.primary
                    score > index && score < starLevel ->
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else ->
                        MaterialTheme.colorScheme.outline
                },
                modifier = Modifier
                    .size(32.dp)
                    .then(
                        if (onRatingClick != null) {
                            Modifier.clickable { onRatingClick(starLevel) }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}
