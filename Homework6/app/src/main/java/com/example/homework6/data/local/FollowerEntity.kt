package com.example.homework6.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "followers")
data class FollowerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageRes: Int = 0,
    val followsYou: Boolean = false,
    val youFollow: Boolean = false,
    val profileOwnerId: Int? = null
)
