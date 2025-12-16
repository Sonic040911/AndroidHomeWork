package com.example.rateverse.ui.screens.topic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rateverse.ui.components.RatingItemCard
import com.example.rateverse.ui.navigation.Screen

@Composable
fun TopicDetailsScreen(
    navController: NavController,
    topicId: Int,
    viewModel: TopicDetailsViewModel = hiltViewModel()
) {
    val items by viewModel.itemsState.collectAsState()
    val topic by viewModel.topicState.collectAsState()

    Scaffold(
        topBar = {
            TopicDetailsTopAppBar(
                title = topic?.title ?: "Loading...",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                if (topic != null) {
                    TopicHeaderSection(
                        title = topic!!.title,
                        creatorName = topic!!.creatorUsername,
                        imageUrl = topic!!.imageUrl
                    )
                }
            }

            item {
                Text(
                    text = "Items (${items.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            items(items, key = { it.item.id }) { itemWithStats ->
                RatingItemCard(
                    itemWithStats = itemWithStats,
                    onClick = {
                        navController.navigate(
                            Screen.ItemDetails.createRoute(itemWithStats.item.id)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailsTopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        }
    )
}

@Composable
fun TopicHeaderSection(
    title: String,
    creatorName: String,
    imageUrl: String?
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        AsyncImage(
            model = imageUrl ?: "https://via.placeholder.com/400x200",
            contentDescription = "Topic cover image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Created by $creatorName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider()
    }
}
