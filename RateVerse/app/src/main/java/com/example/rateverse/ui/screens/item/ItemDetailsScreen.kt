package com.example.rateverse.ui.screens.item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rateverse.ui.components.RatingDistributionSection
import com.example.rateverse.ui.components.ReviewCommentCard
import com.example.rateverse.ui.components.StarRatingIndicator
import com.example.rateverse.ui.components.ReviewDialog

@Composable
fun ItemDetailsScreen(
    navController: NavController,
    itemId: Int,
    viewModel: ItemDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val item = uiState.item

    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableIntStateOf(0) }

    val averageScore = uiState.reviews
        .map { it.review.rating }
        .average()
        .let { if (it.isNaN()) 0.0 else it }

    if (showReviewDialog) {
        ReviewDialog(
            initialRating = selectedRating,
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                viewModel.submitReview(
                    userId = 1,
                    score = rating,
                    comment = comment
                )
                showReviewDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            ItemDetailsTopAppBar(
                title = item?.name ?: "Details",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- Header ----------
            item {
                if (item != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AsyncImage(
                            model = item.imageUrl ?: "https://via.placeholder.com/400x200",
                            contentDescription = "Item image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Created by ${uiState.topicCreatorName ?: "Unknown user"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ---------- Rating summary ----------
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(0.35f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = String.format("%.1f", averageScore),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            StarRatingIndicator(
                                score = averageScore,
                                modifier = Modifier.height(14.dp)
                            )

                            Text(
                                text = "${uiState.reviews.size} reviews",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Box(modifier = Modifier.weight(0.65f)) {
                            RatingDistributionSection(
                                distribution = uiState.distribution
                            )
                        }
                    }
                }
            }

            // ---------- Rate prompt ----------
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme
                            .primaryContainer
                            .copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Would you like to rate this item?",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        StarRatingIndicator(
                            score = 0.0,
                            onRatingClick = { rating ->
                                selectedRating = rating
                                showReviewDialog = true
                            }
                        )
                    }
                }
            }

            // ---------- Reviews ----------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "All reviews (${uiState.reviews.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(uiState.reviews, key = { it.review.id }) { reviewWithUser ->
                ReviewCommentCard(
                    reviewWithUser = reviewWithUser,
                    onLikeClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsTopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        }
    )
}
