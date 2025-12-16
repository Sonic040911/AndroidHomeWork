package com.example.rateverse.data.database

import androidx.room.Embedded
import androidx.room.Relation

data class ReviewWithUser(
    @Embedded val review: ReviewEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity
)