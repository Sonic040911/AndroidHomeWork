package com.example.homework6.di

import android.content.Context
import androidx.room.Room
import com.example.homework6.data.local.AppDatabase
import com.example.homework6.data.local.FollowerDao
import com.example.homework6.data.local.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFollowerDao(db: AppDatabase): FollowerDao = db.followerDao()

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()
}
