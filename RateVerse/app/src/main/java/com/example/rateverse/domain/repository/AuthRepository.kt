package com.example.rateverse.domain.repository

import com.example.rateverse.data.database.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginUser(username: String, passwordHash: String): UserEntity?

    suspend fun registerUser(username: String, email: String, passwordHash: String): Long

    fun getCurrentUser(): Flow<UserEntity?>

    fun logout()
}