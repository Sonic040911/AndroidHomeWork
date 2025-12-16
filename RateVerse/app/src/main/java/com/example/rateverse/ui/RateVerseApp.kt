package com.example.rateverse.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rateverse.ui.navigation.Screen
import com.example.rateverse.ui.screens.auth.AuthScreen
import com.example.rateverse.ui.screens.create.CreateTopicScreen
import com.example.rateverse.ui.screens.home.HomeScreen
import com.example.rateverse.ui.screens.item.ItemDetailsScreen
import com.example.rateverse.ui.screens.topic.TopicDetailsScreen

@Composable
fun RateVerseApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Auth.route) {
            AuthScreen(navController = navController)
        }

        composable(Screen.CreateTopic.route) {
            CreateTopicScreen(navController = navController)
        }

        composable(
            route = Screen.ItemDetails.route,
            arguments = Screen.ItemDetails.navArguments
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            ItemDetailsScreen(
                navController = navController,
                itemId = itemId
            )
        }

        composable(
            route = Screen.TopicDetails.route,
            arguments = Screen.TopicDetails.navArguments
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            TopicDetailsScreen(
                navController = navController,
                topicId = topicId
            )
        }

        composable(Screen.Profile.route) {
            androidx.compose.material3.Text(
                text = "Profile page coming soon"
            )
        }
    }
}
