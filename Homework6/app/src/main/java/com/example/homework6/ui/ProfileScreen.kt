package com.example.homework6.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.homework6.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, vm: ProfileViewModel, isOwnProfile: Boolean) {
    val state by vm.ui.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(vm) {
        vm.events.collect { e ->
            when (e) {
                is com.example.homework6.viewmodel.UiEvent.ShowSnackbar -> {
                    val res = snackbarHostState.showSnackbar(
                        message = e.message,
                        actionLabel = e.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (res == androidx.compose.material3.SnackbarResult.ActionPerformed && e.actionLabel == "Undo") {
                        vm.undoRemove()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(if (isOwnProfile) "${state.name}'s Page" else "Profile") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.Home, contentDescription = "Go Home")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* optional */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    }
                )
                Divider(color = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isOwnProfile) {
                item {
                    StoriesCarousel(
                        followers = state.followers,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }

            item {
                ProfileCardVariant(
                    name = state.name,
                    bio = state.bio,
                    imageRes = com.example.homework6.R.drawable.ic_launcher_foreground,
                    followerCount = state.followerCount,
                    isFollowing = state.isFollowingProfile,
                    isOwn = isOwnProfile,
                    onFollowToggle = {
                        vm.toggleProfileFollowing()
                        val msg = if (!state.isFollowingProfile) "You are now following ${state.name}" else "You unfollowed ${state.name}"
                        vm.sendEvent(com.example.homework6.viewmodel.UiEvent.ShowSnackbar(msg))
                    },
                    onEdit = {
                        navController.navigate("edit")
                    }
                )
            }

            item {
                Text(
                    text = "Followers",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    fontSize = 18.sp

                )
            }

            items(state.followers, key = { it.id }) { follower ->
                FollowerItem(
                    follower = follower,
                    onToggleFollow = { f -> vm.toggleFollowerYouFollow(f.id) },
                    onRemove = { f ->
                        vm.removeFollower(f.id)
                        scope.launch {
                            vm.sendEvent(com.example.homework6.viewmodel.UiEvent.ShowSnackbar("${f.name} removed", actionLabel = "Undo"))
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
