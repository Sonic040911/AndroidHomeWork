package com.example.homework6.data.remote

import retrofit2.http.GET

data class UserDto(
    val id: Int,
    val name: String?,
    val email: String?
)

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}
