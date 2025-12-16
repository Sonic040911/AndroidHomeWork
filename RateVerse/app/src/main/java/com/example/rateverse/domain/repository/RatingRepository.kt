package com.example.rateverse.domain.repository

import com.example.rateverse.data.database.UserEntity
import com.example.rateverse.domain.model.TopicWithStats
import kotlinx.coroutines.flow.Flow

interface RatingRepository {
    fun getCurrentUser(): Flow<UserEntity?>

    suspend fun loginUser(username: String, passwordHash: String): UserEntity?

    suspend fun registerUser(username: String, email: String, passwordHash: String): Long

    fun getAllTopicsWithStats(): Flow<List<TopicWithStats>>

    suspend fun createTopic(
        title: String,
        description: String?,
        imageUrl: String?,
        creatorId: Int
    ): Long

    suspend fun refreshInitialData()
}