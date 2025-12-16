package com.example.rateverse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        TopicEntity::class,
        ItemEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun topicDao(): TopicDao
    abstract fun itemDao(): ItemDao
    abstract fun reviewDao(): ReviewDao
}