package com.example.rateverse.di

import com.example.rateverse.data.repository.AuthRepositoryImpl
import com.example.rateverse.data.repository.ReviewRepositoryImpl
import com.example.rateverse.data.repository.TopicRepositoryImpl
import com.example.rateverse.domain.repository.AuthRepository
import com.example.rateverse.domain.repository.ReviewRepository
import com.example.rateverse.domain.repository.TopicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTopicRepository(
        topicRepositoryImpl: TopicRepositoryImpl
    ): TopicRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository
}