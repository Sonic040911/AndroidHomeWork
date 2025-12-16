package com.example.rateverse.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun submitReview(review: ReviewEntity)

    @Transaction
    @Query("SELECT * FROM reviews WHERE itemId = :itemId ORDER BY createdAt DESC")
    fun getReviewsWithUserByItemId(itemId: Int): Flow<List<ReviewWithUser>>

    @Query("SELECT * FROM reviews WHERE userId = :userId AND itemId = :itemId")
    suspend fun getReviewByUserIdAndItemId(userId: Int, itemId: Int): ReviewEntity?
}