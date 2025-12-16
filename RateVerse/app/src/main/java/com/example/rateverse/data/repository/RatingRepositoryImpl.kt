package com.example.rateverse.data.repository

import com.example.rateverse.data.api.ApiService
import com.example.rateverse.data.database.TopicDao
import com.example.rateverse.data.database.UserDao
import com.example.rateverse.data.database.UserEntity
import com.example.rateverse.domain.model.TopicWithStats
import com.example.rateverse.domain.repository.RatingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.rateverse.data.database.TopicEntity
import android.util.Log
import com.example.rateverse.data.database.ItemDao
import com.example.rateverse.data.database.ReviewDao

class RatingRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val topicDao: TopicDao,
    private val itemDao: ItemDao,
    private val reviewDao: ReviewDao,
    private val apiService: ApiService
) : RatingRepository {
    private val TAG = "RatingRepository"

    override fun getCurrentUser(): Flow<UserEntity?> {
        return kotlinx.coroutines.flow.flowOf(null)
    }

    override suspend fun loginUser(username: String, passwordHash: String): UserEntity? {
        return userDao.getUserByUsername(username)?.takeIf {
            it.passwordHash == passwordHash
        }
    }

    override suspend fun registerUser(username: String, email: String, passwordHash: String): Long {
        val newUser = UserEntity(
            username = username,
            email = email,
            passwordHash = passwordHash
        )
        return userDao.registerUser(newUser)
    }


    override fun getAllTopicsWithStats(): Flow<List<TopicWithStats>> {
        return topicDao.getAllTopicsWithStats()
    }

    override suspend fun createTopic(
        title: String,
        description: String?,
        imageUrl: String?,
        creatorId: Int
    ): Long {
        val newTopic = TopicEntity(
            title = title,
            description = description,
            imageUrl = imageUrl,
            creatorId = creatorId
        )
        return topicDao.insertTopic(newTopic)
    }

    override suspend fun refreshInitialData() {
        try {
            val response = apiService.getInitialTopicsSeed()

            if (response.isSuccessful && response.body() != null) {
                val mockTopics = response.body()!!
                mockTopics.forEach { dto ->
                    val topicEntity = TopicEntity(
                        title = dto.title,
                        description = dto.description,
                        imageUrl = dto.imageUrl,
                        creatorId = dto.creatorId
                    )
                    topicDao.insertTopic(topicEntity)
                }
                Log.d(TAG, "Initial topics data refreshed successfully.")
            } else {
                Log.e(TAG, "API call failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing initial data: ${e.message}")
        }
    }
}