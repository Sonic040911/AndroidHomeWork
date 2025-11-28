package com.example.homework6.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework6.data.local.FollowerEntity
import com.example.homework6.data.local.PostEntity
import com.example.homework6.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Post(
    val id: Int,
    val username: String,
    val content: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val comments: List<String> = emptyList()
)

data class Follower(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val followsYou: Boolean = false,
    val youFollow: Boolean = false
)

data class ProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "MaoZeDong",
    val bio: String = "Android learner, Compose beginner",
    val followerCount: Int = 10,
    val isFollowingProfile: Boolean = false,
    val followers: List<Follower> = emptyList(),
    val posts: List<Post> = emptyList()
)

sealed interface UiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : UiEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui = _ui.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>(replay = 1)
    val events = _events.asSharedFlow()


    private fun PostEntity.toDomain(): Post = Post(
        id = this.id,
        username = this.username,
        content = this.content,
        likes = this.likes,
        isLiked = this.isLiked,
        comments = this.comments
    )
    private fun Post.toEntity(): PostEntity = PostEntity(
        id = this.id,
        username = this.username,
        content = this.content,
        likes = this.likes,
        isLiked = this.isLiked,
        comments = this.comments
    )

    private fun FollowerEntity.toDomain(): Follower = Follower(
        id = this.id,
        name = this.name,
        imageRes = this.imageRes,
        followsYou = this.followsYou,
        youFollow = this.youFollow
    )
    private fun Follower.toEntity(profileOwnerId: Int = 1): FollowerEntity = FollowerEntity(
        id = this.id,
        name = this.name,
        imageRes = this.imageRes,
        followsYou = this.followsYou,
        youFollow = this.youFollow,
        profileOwnerId = profileOwnerId
    )


    init {
        viewModelScope.launch {
            launch {
                repo.getProfileFromLocal().collect { entity ->
                    if (entity != null) {
                        _ui.update { state -> state.copy(name = entity.name, bio = entity.bio) }
                    } else {
                        repo.updateProfile(_ui.value.name, _ui.value.bio)
                    }
                }
            }

            if (repo.getAllFollowers().first().isEmpty()) {
                repo.refreshProfiles()
            }
            launch {
                repo.getAllFollowers()
                    .map { entities -> entities.map { it.toDomain() } }
                    .collect { followers ->
                        _ui.update { it.copy(followers = followers) }
                    }
            }

            if (repo.getPostsFromLocal().first().isEmpty()) {
                val initialPosts = listOf(
                    PostEntity(id = 1, username = "MaoZeDong", content = "Homework is finally finished!", likes = 5, isLiked = false, comments = listOf("YES!!")),
                    PostEntity(id = 2, username = "Stalin", content = "Kotlin is a piece of shit.", likes = 10, isLiked = true, comments = emptyList())
                )
                repo.insertPosts(initialPosts)
            }
            launch {
                repo.getPostsFromLocal()
                    .map { entities -> entities.map { it.toDomain() } }
                    .collect { posts ->
                        _ui.update { it.copy(posts = posts) }
                    }
            }
        }
    }

    fun togglePostLike(postId: Int) {
        viewModelScope.launch {
            val currentPost = _ui.value.posts.find { it.id == postId } ?: return@launch
            val newLikes = if (currentPost.isLiked) currentPost.likes - 1 else currentPost.likes + 1
            val updatedPost = currentPost.copy(likes = newLikes, isLiked = !currentPost.isLiked)
            repo.updatePost(updatedPost.toEntity())
        }
    }

    fun addComment(postId: Int, comment: String) {
        if (comment.isBlank()) return
        viewModelScope.launch {
            val currentPost = _ui.value.posts.find { it.id == postId } ?: return@launch
            val updatedPost = currentPost.copy(comments = currentPost.comments + comment)
            repo.updatePost(updatedPost.toEntity())
        }
    }

    private fun sendEventInternal(e: UiEvent) {
        viewModelScope.launch { _events.emit(e) }
    }

    fun sendEvent(e: UiEvent) {
        sendEventInternal(e)
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true) }
            try {
                delay(1000)
                repo.refreshProfiles()
                sendEvent(UiEvent.ShowSnackbar("Refreshed"))
            } catch (e: Exception) {
                sendEvent(UiEvent.ShowSnackbar("Refresh failed: ${e.message ?: "unknown"}"))
            } finally {
                _ui.update { it.copy(isLoading = false) }
            }
        }
    }

    private var lastRemoved: Follower? = null
    private var lastRemovedIndex: Int? = null

    fun setName(n: String) {
        _ui.update { it.copy(name = n) }
        viewModelScope.launch {
            repo.updateProfile(n, _ui.value.bio)
        }
    }

    fun setBio(b: String) {
        _ui.update { it.copy(bio = b) }
        viewModelScope.launch {
            repo.updateProfile(_ui.value.name, b)
        }
    }

    fun toggleProfileFollowing() {
        val current = _ui.value
        val now = !current.isFollowingProfile
        val delta = if (now) 1 else -1
        _ui.value = current.copy(
            isFollowingProfile = now,
            followerCount = (current.followerCount + delta).coerceAtLeast(0)
        )
    }

    fun toggleFollowerYouFollow(followerId: Int) {
        val currentFollowers = _ui.value.followers
        val updatedFollowers = currentFollowers.map { follower ->
            if (follower.id == followerId) {
                val updated = follower.copy(youFollow = !follower.youFollow)

                viewModelScope.launch {
                    repo.updateFollowerStatus(updated.toEntity())
                }

                updated
            } else {
                follower
            }
        }
        _ui.update { it.copy(followers = updatedFollowers) }
    }

    fun removeFollower(id: Int) {
        val cur = _ui.value
        val index = cur.followers.indexOfFirst { it.id == id }
        if (index >= 0) {
            lastRemoved = cur.followers[index]
            lastRemovedIndex = index
            val newList = cur.followers.toMutableList().also { it.removeAt(index) }
            val followerCountDelta = if (lastRemoved?.followsYou == true) -1 else 0
            _ui.value = cur.copy(
                followers = newList,
                followerCount = (cur.followerCount + followerCountDelta).coerceAtLeast(0)
            )
        }
    }

    fun undoRemove() {
        val cur = _ui.value
        val removed = lastRemoved ?: return
        val idx = lastRemovedIndex ?: cur.followers.size
        val newList = cur.followers.toMutableList().also { it.add(idx, removed) }
        val followerCountDelta = if (removed.followsYou) 1 else 0
        _ui.value = cur.copy(
            followers = newList,
            followerCount = (cur.followerCount + followerCountDelta).coerceAtLeast(0)
        )
        lastRemoved = null
        lastRemovedIndex = null
    }
}