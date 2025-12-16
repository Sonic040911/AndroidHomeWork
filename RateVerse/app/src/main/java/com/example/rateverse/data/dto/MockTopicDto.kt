package com.example.rateverse.data.dto

import com.google.gson.annotations.SerializedName

data class MockTopicDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("creator_id") val creatorId: Int
)