package com.example.rateverse.ui.screens.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateverse.data.database.ItemEntity
import com.example.rateverse.data.database.ReviewWithUser
import com.example.rateverse.domain.model.RatingDistribution
import com.example.rateverse.domain.repository.ReviewRepository
import com.example.rateverse.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemDetailsUiState(
    val item: ItemEntity? = null,
    val topicCreatorId: Int? = null,
    val topicCreatorName: String? = null,
    val distribution: RatingDistribution = RatingDistribution(),
    val reviews: List<ReviewWithUser> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val topicRepository: TopicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle["itemId"])

    private val _uiState = MutableStateFlow(ItemDetailsUiState())
    val uiState: StateFlow<ItemDetailsUiState> = _uiState.asStateFlow()

    private val reviewsFlow = reviewRepository.getReviewsByItemId(itemId)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val itemEntity = reviewRepository.getItemById(itemId)
                if (itemEntity == null) {
                    _uiState.update {
                        it.copy(
                            error = "Item not found",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                val topicWithStats =
                    topicRepository.getTopicDetailsById(itemEntity.topicId)

                reviewsFlow.collect { reviews ->
                    val distribution = calculateDistribution(reviews)
                    _uiState.update {
                        it.copy(
                            item = itemEntity,
                            topicCreatorId = topicWithStats?.topicId,
                            topicCreatorName = topicWithStats?.creatorUsername,
                            distribution = distribution,
                            reviews = reviews,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to load data: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun calculateDistribution(
        reviews: List<ReviewWithUser>
    ): RatingDistribution {
        val counts = reviews
            .groupingBy { it.review.rating.toInt() }
            .eachCount()

        return RatingDistribution(
            score1Count = counts[1] ?: 0,
            score2Count = counts[2] ?: 0,
            score3Count = counts[3] ?: 0,
            score4Count = counts[4] ?: 0,
            score5Count = counts[5] ?: 0
        )
    }

    fun submitReview(
        userId: Int,
        score: Int,
        comment: String?
    ) {
        viewModelScope.launch {
            try {
                reviewRepository.submitReview(
                    userId = userId,
                    itemId = itemId,
                    score = score,
                    comment = comment
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to submit review: ${e.message}"
                    )
                }
            }
        }
    }
}
