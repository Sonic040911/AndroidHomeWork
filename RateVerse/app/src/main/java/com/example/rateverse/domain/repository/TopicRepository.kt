package com.example.rateverse.domain.repository

import com.example.rateverse.data.database.TopicEntity
import com.example.rateverse.domain.model.TopicWithStats
import com.example.rateverse.ui.screens.create.DraftItem
import kotlinx.coroutines.flow.Flow

interface TopicRepository {

    fun getAllTopicsWithStats(): Flow<List<TopicWithStats>>

    fun getAllTopicsWithTopItems(): Flow<List<TopicWithStats>>

    suspend fun createTopic(
        title: String,
        description: String?,
        imageUrl: String?,
        creatorId: Int
    ): Long

    suspend fun getTopicById(topicId: Int): TopicEntity?

    suspend fun refreshInitialData()

    suspend fun addTopicItems(
        topicId: Int,
        items: List<DraftItem>
    )

    suspend fun getTopicDetailsById(topicId: Int): TopicWithStats?
}
