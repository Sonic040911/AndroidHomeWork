package com.example.homework6.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.homework6.data.converter.ListConverter

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val content: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val comments: List<String> = emptyList()
)