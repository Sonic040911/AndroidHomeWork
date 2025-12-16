package com.example.rateverse.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rateverse.ui.components.TopicCard
import com.example.rateverse.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val topics by viewModel.topicsState.collectAsState()
    val currentUser by viewModel.currentUserState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopAppBar(
                isLoggedIn = currentUser != null,
                onUserIconClick = {
                    navController.navigate(Screen.Auth.route)
                },
                onCreateTopicClick = {
                    navController.navigate(Screen.CreateTopic.route)
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Recommended for you",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(topics, key = { it.topicId }) { topic ->
                TopicCard(
                    topic = topic,
                    onClick = {
                        navController.navigate(
                            Screen.TopicDetails.createRoute(topic.topicId)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    isLoggedIn: Boolean,
    onUserIconClick: () -> Unit,
    onCreateTopicClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Rateverse") },
        navigationIcon = {
            IconButton(onClick = onCreateTopicClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create topic"
                )
            }
        },
        actions = {
            IconButton(onClick = onUserIconClick) {
                if (isLoggedIn) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Log in or sign up"
                    )
                }
            }
        }
    )
}
