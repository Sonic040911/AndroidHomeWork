package com.example.homework6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import dagger.hilt.android.AndroidEntryPoint
import com.example.homework6.ui.HomeScreen
import com.example.homework6.ui.ProfileScreen
import com.example.homework6.ui.EditProfileScreen
import com.example.homework6.viewmodel.ProfileViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    MaterialTheme {
        val navController = rememberNavController()
        val vm: ProfileViewModel = hiltViewModel()

        NavHost(navController = navController, startDestination = Routes.HOME) {

            composable(Routes.HOME) {
                HomeScreen(navController = navController, vm = vm)
            }

            composable(
                route = Routes.PROFILE,
                arguments = listOf(navArgument("isOwn") { type = NavType.StringType })
            ) { backStackEntry ->
                val isOwn = backStackEntry.arguments?.getString("isOwn") == "true"
                ProfileScreen(navController = navController, vm = vm, isOwnProfile = isOwn)
            }

            composable(Routes.EDIT) {
                EditProfileScreen(navController = navController, vm = vm)
            }
        }
    }
}
