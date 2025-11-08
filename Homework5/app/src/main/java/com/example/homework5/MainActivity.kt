package com.example.homework5

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Home

data class Follower(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val followsYou: Boolean = false,
    val youFollow: Boolean = false
)

data class ProfileUiState(
    val name: String = "MaoZeDong",
    val bio: String = "Android learner, Compose beginner",
    val followerCount: Int = 10,
    val isFollowingProfile: Boolean = false,
    val followers: List<Follower> = emptyList()
)

sealed interface UiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : UiEvent
}

class ProfileViewModel : ViewModel() {
    private val initialFollowers = listOf(
        Follower(1, "Hitler", R.drawable.hitler, followsYou = true, youFollow = true),
        Follower(2, "Mussolini", R.drawable.mussolini, followsYou = true, youFollow = false),
        Follower(3, "Jiang-Jie-Shi", R.drawable.jiang_jie_shi, followsYou = false, youFollow = true),
        Follower(4, "Emperor Showa", R.drawable.showa, followsYou = true, youFollow = false),
        Follower(5, "Stalin", R.drawable.stalin, followsYou = false, youFollow = false)
    )

    // stateflow holds ui state for compose to collect
    private val _ui = MutableStateFlow(
        ProfileUiState(
            name = "MaoZeDong",
            bio = "Android learner, Compose beginner",
            followerCount = 10,
            isFollowingProfile = false,
            followers = initialFollowers
        )
    )
    val ui = _ui.asStateFlow()

    // sharedflow for one-time ui events (snackbar)
    private val _events = MutableSharedFlow<UiEvent>(replay = 1)
    val events = _events.asSharedFlow()

    // helper to emit events from ui
    fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _events.emit(event) }
    }

    // store last removed for undo
    private var lastRemoved: Follower? = null
    private var lastRemovedIndex: Int? = null

    // update name in state
    fun setName(n: String) {
        _ui.value = _ui.value.copy(name = n)
    }

    // update bio in state
    fun setBio(b: String) {
        _ui.value = _ui.value.copy(bio = b)
    }

    // toggle follow state for profile (increase/decrease follower count)
    fun toggleProfileFollowing() {
        val current = _ui.value
        val now = !current.isFollowingProfile
        val followerDelta = if (now) 1 else -1
        _ui.value = current.copy(
            isFollowingProfile = now,
            followerCount = (current.followerCount + followerDelta).coerceAtLeast(0)
        )
    }

    // toggle youFollow for a follower item
    fun toggleFollowerYouFollow(id: Int) {
        val cur = _ui.value
        val newList = cur.followers.map {
            if (it.id == id) it.copy(youFollow = !it.youFollow) else it
        }
        _ui.value = cur.copy(followers = newList)
    }

    // add follower to list
    fun addFollower(f: Follower) {
        val cur = _ui.value
        _ui.value = cur.copy(followers = cur.followers + f)
    }

    // remove follower and keep it for undo
    fun removeFollower(id: Int) {
        val cur = _ui.value
        val index = cur.followers.indexOfFirst { it.id == id }
        if (index >= 0) {
            lastRemoved = cur.followers[index]
            lastRemovedIndex = index
            val newList = cur.followers.toMutableList().also { it.removeAt(index) }
            val followerCountDelta = if (lastRemoved?.followsYou == true) -1 else 0
            _ui.value = cur.copy(followers = newList, followerCount = (cur.followerCount + followerCountDelta).coerceAtLeast(0))
        }
    }

    // undo last remove
    fun undoRemove() {
        val cur = _ui.value
        val removed = lastRemoved ?: return
        val idx = lastRemovedIndex ?: cur.followers.size
        val newList = cur.followers.toMutableList().also { it.add(idx, removed) }
        val followerCountDelta = if (removed.followsYou) 1 else 0
        _ui.value = cur.copy(followers = newList, followerCount = cur.followerCount + followerCountDelta)
        lastRemoved = null
        lastRemovedIndex = null
    }
}

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile/{isOwn}"
    const val EDIT = "edit"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val vm: ProfileViewModel = viewModel()
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, vm: ProfileViewModel) {
    val state by vm.ui.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // collect events to show snackbar on home
    LaunchedEffect(vm) {
        vm.events.collect { e ->
            when (e) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(e.message, actionLabel = e.actionLabel)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home Page") },
                navigationIcon = {
                    IconButton(onClick = {
                        // open own profile
                        navController.navigate("profile/true")
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "My avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* optional */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                StoriesCarousel(followers = state.followers, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp))
            }

            items(6) { idx ->
                SimplePostLine(
                    username = state.name,
                    caption = "Placeholder post #${idx + 1} — a short single-line caption.",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SimplePostLine(username: String, caption: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "post-avatar",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(username, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text(caption, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp, color = Color.DarkGray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, vm: ProfileViewModel, isOwnProfile: Boolean) {
    val state by vm.ui.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // collect one-time events and handle undo
    LaunchedEffect(vm) {
        vm.events.collect { e ->
            when (e) {
                is UiEvent.ShowSnackbar -> {
                    val res = snackbarHostState.showSnackbar(e.message, actionLabel = e.actionLabel, duration = SnackbarDuration.Short)
                    if (res == SnackbarResult.ActionPerformed && e.actionLabel == "Undo") {
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
            if (!isOwnProfile) {
                item {
                    StoriesCarousel(followers = state.followers, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp))
                }
            }

            item {
                ProfileCardVariant(
                    name = state.name,
                    bio = state.bio,
                    imageRes = R.drawable.avatar,
                    followerCount = state.followerCount,
                    isFollowing = state.isFollowingProfile,
                    isOwn = isOwnProfile,
                    onFollowToggle = {
                        vm.toggleProfileFollowing()
                        val msg = if (!state.isFollowingProfile) "You are now following ${state.name}" else "You unfollowed ${state.name}"
                        vm.sendEvent(UiEvent.ShowSnackbar(msg))
                    },
                    onEdit = {
                        navController.navigate(Routes.EDIT)
                    }
                )
            }

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

            items(state.followers, key = { it.id }) { follower ->
                FollowerItem(
                    follower = follower,
                    onToggleFollow = { f -> vm.toggleFollowerYouFollow(f.id) },
                    onRemove = { f ->
                        vm.removeFollower(f.id)
                        scope.launch {
                            vm.sendEvent(UiEvent.ShowSnackbar("${f.name} removed", actionLabel = "Undo"))
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
fun ProfileCardVariant(
    name: String,
    bio: String,
    imageRes: Int,
    followerCount: Int,
    isFollowing: Boolean,
    isOwn: Boolean,
    onFollowToggle: () -> Unit,
    onEdit: () -> Unit
) {
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
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Profile Avatar",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = bio,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Followers: $followerCount",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!isOwn) {
                    Button(
                        onClick = { onFollowToggle() },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                    ) {
                        Text(text = if (isFollowing) "Unfollow" else "Follow")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onEdit) {
                        Text("Edit Profile")
                    }
                } else {
                    Button(onClick = onEdit, modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp)) {
                        Text("Edit Profile")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, vm: ProfileViewModel) {
    val state by vm.ui.collectAsState()
    var name by rememberSaveable { mutableStateOf(state.name) }
    var bio by rememberSaveable { mutableStateOf(state.bio) }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Edit Profile") })
    }) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Preview", fontWeight = FontWeight.SemiBold)
                    Text(name, fontSize = 16.sp)
                    Text(bio, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(Modifier.height(20.dp))

            Row {
                Button(onClick = {
                    vm.setName(name)
                    vm.setBio(bio)
                    vm.sendEvent(UiEvent.ShowSnackbar("Profile updated"))
                    navController.popBackStack()
                }) {
                    Text("Save")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun StoriesCarousel(followers: List<Follower>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(followers, key = { it.id }) { follower ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { /* open story */ }
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
