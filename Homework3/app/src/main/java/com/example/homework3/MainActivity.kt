package com.example.homework3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProfileScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Profile") }, // Title
                    navigationIcon = {
                        // avatar icon
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile Icon"
                            )
                        }
                    },
                    actions = {
                        // ... icon
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }
                    }
                )
                // Top Bar Under Line
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            ProfileCard(
                name = "MaoZeDong",
                bio = "Android learner, Compose beginner",
                imageRes = R.drawable.avatar,
                onFollow = {
                    // snackbar hint when following
                    scope.launch {
                        snackbarHostState.showSnackbar("You are now following ${it}")
                    }
                }
            )
        }
    }
}


@Composable
fun ProfileCard(name: String, bio: String, imageRes: Int, onFollow: (String) -> Unit) {
    var isFollowing by rememberSaveable { mutableStateOf(false) }
    var followerCount by rememberSaveable { mutableStateOf(10) }
    var showDialog by remember { mutableStateOf(false) }

    // followed -> green, unfollowed -> blue
    val buttonColor by animateColorAsState(
        if (isFollowing) Color(0xFF4CAF50) else Color(0xFF2196F3)
    )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Profile Avatar",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // User name
                Text(
                    text = name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Info
                Text(
                    text = bio,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fans count
                Text(
                    text = "Followers: $followerCount",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Follow / Unfollow
                Button(
                    onClick = {
                        if (isFollowing) {
                            showDialog = true
                        } else {
                            isFollowing = true
                            followerCount++
                            onFollow(name)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(text = if (isFollowing) "Unfollow" else "Follow")
                }

                Spacer(modifier = Modifier.height(8.dp))


                OutlinedButton(onClick = {}) {
                    Text("More Info")
                }
            }
        }
    }

    // when unfollowing pop up a confirmation
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    isFollowing = false
                    followerCount--
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Unfollow Confirmation") },
            text = { Text("Are you sure you want to unfollow $name?") }
        )
    }
}
