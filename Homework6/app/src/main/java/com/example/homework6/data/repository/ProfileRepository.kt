package com.example.homework6.data.repository

import android.util.Log
import com.example.homework6.data.local.FollowerDao
import com.example.homework6.data.local.FollowerEntity
import com.example.homework6.data.local.PostDao
import com.example.homework6.data.local.PostEntity
import com.example.homework6.data.local.ProfileEntity
import com.example.homework6.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val followerDao: FollowerDao,
    private val api: ApiService,
    private val postDao: PostDao
) {

    fun getAllFollowers(): Flow<List<FollowerEntity>> = followerDao.getAllFollowers()

    suspend fun refreshProfiles() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("Repo", "Starting refreshProfiles()")
                val users = api.getUsers()
                Log.d("Repo", "Fetched users: ${users.size}")

                val newFollowers = users.map { u ->
                    FollowerEntity(
                        id = u.id,
                        name = u.name ?: "no-name",
                        imageRes = com.example.homework6.R.drawable.ic_launcher_foreground,
                        followsYou = false,
                        youFollow = false,
                        profileOwnerId = 1
                    )
                }

                followerDao.deleteAllFollowers()

                newFollowers.forEach { followerDao.insertFollower(it) }

                Log.d("Repo", "DB updated: ${newFollowers.size} followers")
            } catch (e: Exception) {
                Log.e("Repo", "refreshProfiles failed", e)
                throw e
            }
        }
    }

    fun getPostsFromLocal(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }

    suspend fun insertPosts(posts: List<PostEntity>) {
        postDao.insertAll(posts)
    }

    suspend fun updatePost(post: PostEntity) {
        postDao.updatePost(post)
    }

    fun getProfileFromLocal(): Flow<ProfileEntity?> = followerDao.getProfile()

    suspend fun updateProfile(name: String, bio: String) {
        val profileEntity = ProfileEntity(name = name, bio = bio)
        followerDao.insertProfile(profileEntity)
    }

    suspend fun updateFollowerStatus(follower: FollowerEntity) {
        followerDao.updateFollower(follower)
    }
}