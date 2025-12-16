package com.example.rateverse.ui.screens.auth

import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rateverse.ui.components.LoginDialog
import com.example.rateverse.ui.components.RegisterDialog

enum class AuthState { LOGIN, REGISTER }

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var currentState by remember { mutableStateOf(AuthState.LOGIN) }
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(viewModel.isLoginSuccessful.value) {
        if (viewModel.isLoginSuccessful.value) {
            navController.popBackStack()
            viewModel.clearError()
        }
    }

    Dialog(onDismissRequest = {
        navController.popBackStack()
        viewModel.clearError()
    }) {
        when (currentState) {
            AuthState.LOGIN -> {
                LoginDialog(
                    onLoginClick = { username, password ->
                        viewModel.login(username, password)
                    },
                    onNavigateToRegister = {
                        currentState = AuthState.REGISTER
                        viewModel.clearError()
                    },
                    onDismiss = {
                        navController.popBackStack()
                        viewModel.clearError()
                    },
                    errorMessage = if (currentState == AuthState.LOGIN) errorMessage else null
                )
            }
            AuthState.REGISTER -> {
                RegisterDialog(
                    onRegisterClick = { username, email, password, confirmPassword ->
                        viewModel.register(username, email, password, confirmPassword)
                    },
                    onNavigateToLogin = {
                        currentState = AuthState.LOGIN
                        viewModel.clearError()
                    },
                    onDismiss = {
                        navController.popBackStack()
                        viewModel.clearError()
                    },
                    errorMessage = if (currentState == AuthState.REGISTER) errorMessage else null
                )
            }
        }
    }
}