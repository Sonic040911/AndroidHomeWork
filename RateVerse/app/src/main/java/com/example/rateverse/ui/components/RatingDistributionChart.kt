package com.example.rateverse.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rateverse.domain.model.RatingDistribution

@Composable
fun RatingDistributionSection(
    distribution: RatingDistribution
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Rating distribution",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        (5 downTo 1).forEach { score ->
            val count = when (score) {
                5 -> distribution.score5Count
                4 -> distribution.score4Count
                3 -> distribution.score3Count
                2 -> distribution.score2Count
                else -> distribution.score1Count
            }

            val percentage = distribution.getPercentage(count)

            DistributionRow(
                score = score,
                percentage = percentage,
                count = count
            )
        }
    }
}

@Composable
private fun DistributionRow(
    score: Int,
    percentage: Float,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$score star",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(36.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        LinearProgressIndicator(
            progress = percentage,
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(32.dp)
        )
    }
}
