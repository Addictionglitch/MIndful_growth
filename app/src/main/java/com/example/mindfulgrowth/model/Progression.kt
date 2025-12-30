package com.example.mindfulgrowth.model

data class ProgressionState(
    val currentXp: Int = 0,
    val nextReward: RewardItem = RewardItem("Cosmetic Aurora", 1000)
)

data class RewardItem(
    val name: String,
    val xpRequired: Int,
    val iconRes: String = "ic_aurora" // Placeholder for resource path
) {
    fun isUnlocked(currentXp: Int): Boolean = currentXp >= xpRequired
}
