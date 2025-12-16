package com.example.rateverse.ui.screens.topic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateverse.data.database.ItemWithStats
import com.example.rateverse.domain.model.TopicWithStats
import com.example.rateverse.domain.repository.ReviewRepository
import com.example.rateverse.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TopicDetailsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val topicRepository: TopicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val topicId: Int = checkNotNull(savedStateHandle["topicId"])

    val itemsState: StateFlow<List<ItemWithStats>> =
        reviewRepository.getItemsWithStatsByTopicId(topicId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val topicState: StateFlow<TopicWithStats?> = flow {
        emit(topicRepository.getTopicDetailsById(topicId))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
}