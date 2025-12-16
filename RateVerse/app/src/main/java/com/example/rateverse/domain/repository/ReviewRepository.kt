package com.example.rateverse.domain.repository

import com.example.rateverse.data.database.ItemEntity
import com.example.rateverse.data.database.ItemWithStats
import com.example.rateverse.data.database.ReviewWithUser
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getItemsWithStatsByTopicId(topicId: Int): Flow<List<ItemWithStats>>

    suspend fun getItemById(itemId: Int): ItemEntity?

    suspend fun submitReview(userId: Int, itemId: Int, score: Int, comment: String?)

    fun getReviewsByItemId(itemId: Int): Flow<List<ReviewWithUser>>
}