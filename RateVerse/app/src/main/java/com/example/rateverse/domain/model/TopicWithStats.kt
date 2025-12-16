package com.example.rateverse.domain.model

import androidx.room.Ignore

data class TopicWithStats(
    val topicId: Int,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val creatorUsername: String,
    val totalItems: Int,
    val totalReviews: Int,

    @Ignore
    var topItemNames: List<String> = emptyList()
) {
    constructor(
        topicId: Int,
        title: String,
        description: String?,
        imageUrl: String?,
        creatorUsername: String,
        totalItems: Int,
        totalReviews: Int
    ) : this(topicId, title, description, imageUrl, creatorUsername, totalItems, totalReviews, emptyList())
}