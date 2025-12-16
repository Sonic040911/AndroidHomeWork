package com.example.rateverse.data.repository

import com.example.rateverse.data.database.ItemDao
import com.example.rateverse.data.database.ItemEntity
import com.example.rateverse.data.database.ItemWithStats
import com.example.rateverse.data.database.ReviewDao
import com.example.rateverse.data.database.ReviewEntity
import com.example.rateverse.data.database.ReviewWithUser
import com.example.rateverse.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val reviewDao: ReviewDao
) : ReviewRepository {

    override fun getItemsWithStatsByTopicId(topicId: Int): Flow<List<ItemWithStats>> {
        return itemDao.getItemsWithStatsByTopicId(topicId)
    }

    override suspend fun getItemById(itemId: Int): ItemEntity? {
        return itemDao.getItemById(itemId)
    }

    override suspend fun submitReview(userId: Int, itemId: Int, score: Int, comment: String?) {
        val review = ReviewEntity(
            userId = userId,
            itemId = itemId,
            rating = score.toDouble(),
            comment = comment
        )
        reviewDao.submitReview(review)
    }

    override fun getReviewsByItemId(itemId: Int): Flow<List<ReviewWithUser>> {
        return reviewDao.getReviewsWithUserByItemId(itemId)
    }
}