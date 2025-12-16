package com.example.rateverse.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Query("""
        SELECT 
            I.*, 
            COUNT(R.id) AS reviewCount, 
            AVG(R.rating) AS averageRating 
        FROM items I
        LEFT JOIN reviews R ON I.id = R.itemId
        WHERE I.topicId = :topicId
        GROUP BY I.id
    """)
    fun getItemsWithStatsByTopicId(topicId: Int): Flow<List<ItemWithStats>>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): ItemEntity?
}