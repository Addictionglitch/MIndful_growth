
package com.example.mindfulgrowth.ui.screens.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Post(
    val id: Int,
    val username: String,
    val userAvatarUrl: String, // Placeholder for URL
    val actionText: String,
    val caption: String,
    val imageUrl: String // Placeholder for URL
)

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val isUnlocked: Boolean = true // Mock: Start unlocked for testing
)

class FeedViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockPosts = List(10) { index ->
            Post(
                id = index,
                username = "User_${(100..999).random()}",
                userAvatarUrl = "",
                actionText = "Completed a ${(25..90).random()}m Deep Work session",
                caption = "Another session in the bag. Growing the digital forest. #focus",
                imageUrl = ""
            )
        }
        _uiState.value = _uiState.value.copy(posts = mockPosts)
    }

    fun toggleLock() {
        _uiState.value = _uiState.value.copy(isUnlocked = !_uiState.value.isUnlocked)
    }
}
