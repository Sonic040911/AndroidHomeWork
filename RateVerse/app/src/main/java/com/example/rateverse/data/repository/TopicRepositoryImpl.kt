package com.example.rateverse.data.repository

import android.util.Log
import com.example.rateverse.data.api.ApiService
import com.example.rateverse.data.database.ItemDao
import com.example.rateverse.data.database.ItemEntity
import com.example.rateverse.data.database.TopicDao
import com.example.rateverse.data.database.TopicEntity
import com.example.rateverse.domain.model.TopicWithStats
import com.example.rateverse.domain.repository.TopicRepository
import com.example.rateverse.ui.screens.create.DraftItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao,
    private val apiService: ApiService,
    private val itemDao: ItemDao,
) : TopicRepository {

    private val TAG = "TopicRepository"

    override fun getAllTopicsWithStats(): Flow<List<TopicWithStats>> {
        return topicDao.getAllTopicsWithStats()
    }

    override fun getAllTopicsWithTopItems(): Flow<List<TopicWithStats>> {
        return topicDao.getAllTopicsWithStats().map { topics ->
            topics.map { topic ->
                val names = topicDao.getTopItemNamesByTopicId(topic.topicId)
                topic.copy(topItemNames = names)
            }
        }
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

    override suspend fun getTopicById(topicId: Int): TopicEntity? {
        return topicDao.getTopicById(topicId)
    }

    override suspend fun addTopicItems(topicId: Int, items: List<DraftItem>) {
        items.forEach { draft ->
            val itemEntity = ItemEntity(
                topicId = topicId,
                name = draft.name,
                description = draft.description,
                imageUrl = draft.imageUrl
            )
            itemDao.insertItem(itemEntity)
        }
    }

    override suspend fun getTopicDetailsById(topicId: Int): TopicWithStats? {
        val stats = topicDao.getTopicWithStatsById(topicId)
        return stats?.let {
            val names = topicDao.getTopItemNamesByTopicId(it.topicId)
            it.copy(topItemNames = names)
        }
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
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing initial data: ${e.message}")
        }
    }
}