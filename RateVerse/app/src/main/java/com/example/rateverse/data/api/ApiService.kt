package com.example.rateverse.data.api

import com.example.rateverse.data.dto.MockTopicDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("topics/initial_seed")
    suspend fun getInitialTopicsSeed(): Response<List<MockTopicDto>>
}