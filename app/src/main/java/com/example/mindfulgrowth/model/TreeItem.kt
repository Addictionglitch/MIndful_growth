package com.example.mindfulgrowth.model

import com.example.mindfulgrowth.R

data class TreeItem(
    val id: Int,
    val name: String,
    val price: Int = 0,
    val isUnlocked: Boolean = false,
    val isSelected: Boolean = false,
    // Holds 5 separate drawables for progression (Sapling -> Ancient)
    val stageResIds: List<Int>
)

// Mock Data: Uses the same icon for now until you have specific stage assets
val defaultTrees = listOf(
    TreeItem(
        id = 1,
        name = "Neon Oak",
        price = 0,
        isUnlocked = true,
        isSelected = true,
        stageResIds = listOf(
            R.drawable.ic_tree_master, // Stage 1 (0-20%)
            R.drawable.ic_tree_master, // Stage 2 (20-40%)
            R.drawable.ic_tree_master, // Stage 3 (40-60%)
            R.drawable.ic_tree_master, // Stage 4 (60-80%)
            R.drawable.ic_tree_master  // Stage 5 (80-100%)
        )
    ),
    TreeItem(
        id = 2,
        name = "Cyber Pine",
        price = 500,
        isUnlocked = false,
        isSelected = false,
        stageResIds = listOf(
            R.drawable.ic_tree_master,
            R.drawable.ic_tree_master,
            R.drawable.ic_tree_master,
            R.drawable.ic_tree_master,
            R.drawable.ic_tree_master
        )
    )
)