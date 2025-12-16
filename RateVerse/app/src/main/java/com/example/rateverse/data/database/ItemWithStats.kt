package com.example.rateverse.data.database

import androidx.room.Embedded

data class ItemWithStats(
    @Embedded val item: ItemEntity,
    val averageRating: Double? = 0.0,
    val reviewCount: Int = 0
)