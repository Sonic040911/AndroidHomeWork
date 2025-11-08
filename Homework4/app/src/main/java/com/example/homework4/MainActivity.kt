package com.example.homework4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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


data class Follower(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val followsYou: Boolean = false,
    val youFollow: Boolean = false
)

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val followers = remember {
        mutableStateListOf(
            Follower(1, "Hitler", R.drawable.hitler, followsYou = true, youFollow = true),
            Follower(2, "Mussolini", R.drawable.mussolini, followsYou = true, youFollow = false),
            Follower(3, "Jiang-Jie-Shi", R.drawable.jiang_jie_shi, followsYou = false, youFollow = true),
            Follower(4, "Emperor Showa", R.drawable.showa, followsYou = true, youFollow = false),
            Follower(5, "Stalin", R.drawable.stalin, followsYou = false, youFollow = false)
        )
    }

    var profileFollowing by rememberSaveable { mutableStateOf(false) }
    var followerCount by rememberSaveable { mutableStateOf(10) }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Profile") },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Story
            item {
                StoriesCarousel(
                    followers = followers,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }

            // Profile card as an item
            item {
                ProfileCard(
                    name = "MaoZeDong",
                    bio = "Android learner, Compose beginner",
                    imageRes = R.drawable.avatar,
                    followerCount = followerCount,
                    isFollowing = profileFollowing,
                    onFollowToggle = { isNowFollowing ->
                        profileFollowing = isNowFollowing
                        followerCount += if (isNowFollowing) 1 else -1
                        if (isNowFollowing) {
                            // optional hint
                            scope.launch {
                                snackbarHostState.showSnackbar("You are now following MaoZeDong")
                            }
                        }
                    }
                )
            }

            // Followers header
            item {
                Text(
                    text = "Followers",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            // Followers list
            items(followers, key = { it.id }) { follower ->

                FollowerItem(
                    follower = follower,
                    onToggleFollow = { f ->
                        val idx = followers.indexOfFirst { it.id == f.id }
                        if (idx >= 0) {
                            val updated = f.copy(youFollow = !f.youFollow)
                            followers[idx] = updated
                        }
                    },
                    onRemove = { f ->
                        val idx = followers.indexOfFirst { it.id == f.id }
                        if (idx >= 0) {
                            val removed = followers.removeAt(idx)
                            // if removed follower actually followed the profile, decrement count
                            if (removed.followsYou) followerCount--

                            // show undo snackbar
                            scope.launch {
                                val res = snackbarHostState.showSnackbar(
                                    message = "${removed.name} removed",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                if (res == SnackbarResult.ActionPerformed) {
                                    followers.add(idx, removed)
                                    if (removed.followsYou) followerCount++
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun StoriesCarousel(followers: List<Follower>, modifier: Modifier = Modifier) {
    // Show followers' avatars and names as the stories carousel (top row)
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(followers) { follower ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { }
            ) {
                Image(
                    painter = painterResource(id = follower.imageRes),
                    contentDescription = "story-${follower.name}",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = follower.name,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    name: String,
    bio: String,
    imageRes: Int,
    followerCount: Int,
    isFollowing: Boolean,
    onFollowToggle: (Boolean) -> Unit
) {
    // Button color controlled by isFollowing from parent
    val buttonColor by animateColorAsState(if (isFollowing) Color(0xFF4CAF50) else Color(0xFF2196F3))

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

                // Fans count (from parent)
                Text(
                    text = "Followers: $followerCount",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Follow / Unfollow
                Button(
                    onClick = {
                        onFollowToggle(!isFollowing)
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
}

@Composable
fun FollowerItem(follower: Follower, onToggleFollow: (Follower) -> Unit, onRemove: (Follower) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = follower.imageRes),
            contentDescription = "follower-avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = follower.name, fontWeight = FontWeight.SemiBold)
            if (follower.followsYou) {
                Text(text = "Follows you", fontSize = 12.sp)
            } else {
                // optional small hint
                Text(text = "", fontSize = 12.sp)
            }
        }

        Button(onClick = { onToggleFollow(follower) }) {
            Text(if (follower.youFollow) "Following" else "Follow")
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = { onRemove(follower) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "remove")
        }
    }
}
