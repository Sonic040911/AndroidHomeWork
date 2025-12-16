package com.example.rateverse.data.database

import androidx.room.*
import com.example.rateverse.domain.model.TopicWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity): Long

    @Query("SELECT * FROM topics WHERE id = :topicId")
    suspend fun getTopicById(topicId: Int): TopicEntity?

    @Query("""
        SELECT 
            T.id AS topicId,
            T.title,
            T.description,
            T.imageUrl,
            U.username AS creatorUsername,
            COUNT(DISTINCT I.id) AS totalItems,
            COUNT(DISTINCT R.id) AS totalReviews
        FROM topics T
        INNER JOIN users U ON T.creatorId = U.id
        LEFT JOIN items I ON T.id = I.topicId
        LEFT JOIN reviews R ON I.id = R.itemId
        GROUP BY T.id
        ORDER BY T.id DESC
    """)
    fun getAllTopicsWithStats(): Flow<List<TopicWithStats>>

    @Query("""
        SELECT 
            T.id AS topicId,
            T.title,
            T.description,
            T.imageUrl,
            U.username AS creatorUsername,
            COUNT(DISTINCT I.id) AS totalItems,
            COUNT(DISTINCT R.id) AS totalReviews
        FROM topics T
        INNER JOIN users U ON T.creatorId = U.id
        LEFT JOIN items I ON T.id = I.topicId
        LEFT JOIN reviews R ON I.id = R.itemId
        WHERE T.id = :topicId 
        GROUP BY T.id
    """)
    suspend fun getTopicWithStatsById(topicId: Int): TopicWithStats?

    @Query("""
        SELECT name 
        FROM items 
        WHERE topicId = :topicId 
        ORDER BY id ASC
        LIMIT 3
    """)
    suspend fun getTopItemNamesByTopicId(topicId: Int): List<String>
}