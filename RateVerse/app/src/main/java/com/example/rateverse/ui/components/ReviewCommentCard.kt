package com.example.rateverse.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rateverse.data.database.ReviewWithUser

@Composable
fun ReviewCommentCard(
    reviewWithUser: ReviewWithUser,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val review = reviewWithUser.review
    val user = reviewWithUser.user

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatarUrl ?: "https://via.placeholder.com/40",
                contentDescription = "${user.username}'s avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleSmall
                )
                StarRatingIndicator(score = review.rating)
            }

            TextButton(onClick = onLikeClick) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.padding(end = 4.dp))
                Text("Like")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!review.comment.isNullOrBlank()) {
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 52.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(start = 52.dp),
            thickness = 0.5.dp
        )
    }
}
