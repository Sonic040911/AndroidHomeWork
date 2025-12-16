package com.example.rateverse.data.repository

import com.example.rateverse.data.database.UserDao
import com.example.rateverse.data.database.UserEntity
import com.example.rateverse.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : AuthRepository {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)

    override fun getCurrentUser(): Flow<UserEntity?> = _currentUser

    override suspend fun loginUser(username: String, passwordHash: String): UserEntity? {
        val user = userDao.getUserByUsername(username)?.takeIf {
            it.passwordHash == passwordHash
        }
        if (user != null) {
            _currentUser.value = user
        }
        return user
    }

    override suspend fun registerUser(username: String, email: String, passwordHash: String): Long {
        val newUser = UserEntity(username = username, email = email, passwordHash = passwordHash)
        return userDao.registerUser(newUser)
    }

    override fun logout() {
        _currentUser.value = null
    }
}