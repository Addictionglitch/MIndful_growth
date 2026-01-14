package com.example.mindfulgrowth.ui.feed

data class FeedPost(
    val id: String,
    val username: String,
    val duration: String,
    val boostCount: Int,
    val isBoostedByMe: Boolean
)

data class FeedUiState(
    val globalActiveUserCount: Int,
    val userTokenBalance: Int,
    val posts: List<FeedPost>
)
