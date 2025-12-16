package com.example.rateverse.ui.screens.create

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateverse.domain.repository.AuthRepository
import com.example.rateverse.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DraftItem(
    val tempId: Int,
    val name: String,
    val imageUrl: String?,
    val description: String? = null
)

@HiltViewModel
class CreateTopicViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val topicTitle = mutableStateOf("")
    val topicDescription = mutableStateOf("")
    val topicCoverUrl = mutableStateOf<String?>(null)

    val draftItems = mutableStateListOf<DraftItem>()

    val creationSuccess = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private var nextItemId = 0

    fun addItem(name: String, imageUrl: String?, description: String?) {
        draftItems.add(
            DraftItem(
                tempId = nextItemId++,
                name = name,
                imageUrl = imageUrl,
                description = description
            )
        )
    }

    fun removeItem(item: DraftItem) {
        draftItems.remove(item)
    }

    fun createTopic() {
        errorMessage.value = null

        if (topicTitle.value.isBlank()) {
            errorMessage.value = "Topic title cannot be empty"
            return
        }

        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser().first()
            val creatorId = currentUser?.id

            if (creatorId == null) {
                errorMessage.value = "Please log in first"
                return@launch
            }

            val newTopicId = topicRepository.createTopic(
                title = topicTitle.value,
                description = topicDescription.value.ifBlank { null },
                imageUrl = topicCoverUrl.value,
                creatorId = creatorId
            )

            if (newTopicId > 0) {
                if (draftItems.isNotEmpty()) {
                    topicRepository.addTopicItems(
                        newTopicId.toInt(),
                        draftItems.toList()
                    )
                }
                creationSuccess.value = true
            } else {
                errorMessage.value = "Failed to create topic"
            }
        }
    }
}
