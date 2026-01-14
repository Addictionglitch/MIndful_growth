package com.example.mindfulgrowth.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class FeedViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        FeedUiState(
            globalActiveUserCount = 1245,
            userTokenBalance = 50,
            posts = generateMockPosts()
        )
    )
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                _uiState.update { currentState ->
                    currentState.copy(
                        globalActiveUserCount = Random.nextInt(1200, 1500)
                    )
                }
            }
        }
    }

    fun onBoostClick(postId: String) {
        _uiState.update { currentState ->
            if (currentState.userTokenBalance > 0) {
                val updatedPosts = currentState.posts.map { post ->
                    if (post.id == postId && !post.isBoostedByMe) {
                        post.copy(
                            boostCount = post.boostCount + 1,
                            isBoostedByMe = true
                        )
                    } else {
                        post
                    }
                }
                currentState.copy(
                    userTokenBalance = currentState.userTokenBalance - 1,
                    posts = updatedPosts
                )
            } else {
                currentState
            }
        }
    }

    private fun generateMockPosts(): List<FeedPost> {
        val usernames = listOf("User_A", "User_B", "User_C", "User_D", "User_E")
        val durations = listOf("25m", "45m", "60m", "15m", "90m")
        return List(10) { index ->
            FeedPost(
                id = "post_$index",
                username = usernames.random(),
                duration = durations.random(),
                boostCount = Random.nextInt(0, 100),
                isBoostedByMe = false
            )
        }
    }
}
