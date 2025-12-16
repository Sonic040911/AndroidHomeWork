package com.example.rateverse.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument

sealed class Screen(val route: String, val navArguments: List<NamedNavArgument> = emptyList()) {
    object Home : Screen("home")
    object Auth : Screen("auth")
    object CreateTopic : Screen("create_topic")

    data object TopicDetails : Screen(
        route = "topic_details/{topicId}",
        navArguments = listOf(navArgument("topicId") { type = androidx.navigation.NavType.IntType })
    ) {
        fun createRoute(topicId: Int) = "topic_details/$topicId"
    }

    data object ItemDetails : Screen(
        route = "item_details/{itemId}",
        navArguments = listOf(navArgument("itemId") { type = androidx.navigation.NavType.IntType })
    ) {
        fun createRoute(itemId: Int) = "item_details/$itemId"
    }

    data object Profile : Screen("profile")
}