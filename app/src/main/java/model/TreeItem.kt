package com.example.mindfulgrowth.model

data class TreeItem(
    val id: Int,
    val name: String,
    val price: Int,
    val imageResId: Int,
    var isUnlocked: Boolean = false,
    var isSelected: Boolean = false
)