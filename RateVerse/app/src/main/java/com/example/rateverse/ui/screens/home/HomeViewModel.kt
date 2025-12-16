package com.example.rateverse.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateverse.data.database.UserEntity
import com.example.rateverse.domain.model.TopicWithStats
import com.example.rateverse.domain.repository.AuthRepository
import com.example.rateverse.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val topicsState: StateFlow<List<TopicWithStats>> =
        topicRepository.getAllTopicsWithTopItems()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val currentUserState: StateFlow<UserEntity?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            topicRepository.refreshInitialData()
        }
    }
}