package com.example.rateverse.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rateverse.data.database.ItemWithStats

@Composable
fun RatingItemCard(
    itemWithStats: ItemWithStats,
    onClick: () -> Unit
) {
    val item = itemWithStats.item
    val rating = itemWithStats.averageRating ?: 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl ?: "https://via.placeholder.com/80",
                contentDescription = "Item image",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format("%.1f", rating),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    StarRatingIndicator(score = rating)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "(${itemWithStats.reviewCount} reviews)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StarRatingIndicator(
    score: Double,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starLevel = index + 1
            val tintColor = when {
                starLevel <= score ->
                    MaterialTheme.colorScheme.primary
                score > index && score < starLevel ->
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                else ->
                    MaterialTheme.colorScheme.outline
            }

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star rating",
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
