package com.example.mindfulgrowth.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindfulgrowth.R
import com.example.mindfulgrowth.model.TreeItem

class CustomizeFragment : Fragment(R.layout.fragment_customize) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerShop)

        // 1. Create Dummy Data
        val trees = listOf(
            TreeItem(1, "Oak Sapling", 0, 0, isUnlocked = true, isSelected = true),
            TreeItem(2, "Pine Tree", 500, 0),
            TreeItem(3, "Cherry Blossom", 1200, 0),
            TreeItem(4, "Bonsai", 2500, 0),
            TreeItem(5, "Golden Tree", 9999, 0),
            TreeItem(6, "Cactus", 300, 0)
        )

        // 2. Connect Adapter
        val adapter = CustomizeAdapter(trees) { clickedTree ->
            Toast.makeText(context, "Clicked: ${clickedTree.name}", Toast.LENGTH_SHORT).show()
        }

        // 3. Set Layout Manager (Grid with 2 columns)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter
    }
}