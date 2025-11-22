package com.example.homework6.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.homework6.data.converter.ListConverter

@Database(
    entities = [FollowerEntity::class, PostEntity::class, ProfileEntity::class],
    version = 3,
    exportSchema = false
)
// 3. 添加 TypeConverters
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun followerDao(): FollowerDao

    abstract fun postDao(): PostDao
}