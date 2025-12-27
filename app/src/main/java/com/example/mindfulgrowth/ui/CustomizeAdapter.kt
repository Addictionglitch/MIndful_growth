package com.example.mindfulgrowth.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mindfulgrowth.R
import com.example.mindfulgrowth.model.TreeItem

class CustomizeAdapter(
    private val items: List<TreeItem>,
    private val onTreeClick: (TreeItem) -> Unit
) : RecyclerView.Adapter<CustomizeAdapter.TreeViewHolder>() {

    class TreeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // We find the CardView by going up the hierarchy from the image
        val card: CardView = view.findViewById<ImageView>(R.id.imgTree).parent.parent as CardView
        val img: ImageView = view.findViewById(R.id.imgTree)
        val name: TextView = view.findViewById(R.id.tvName)
        val price: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_tree, parent, false)
        return TreeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name

        // Logic to highlight selected item vs locked items
        if (item.isSelected) {
            holder.card.setCardBackgroundColor(Color.parseColor("#33C69C6D")) // Dim Gold
            holder.price.text = "Selected"
        } else if (item.isUnlocked) {
            holder.card.setCardBackgroundColor(Color.parseColor("#1E1E1E"))
            holder.price.text = "Owned"
        } else {
            holder.card.setCardBackgroundColor(Color.parseColor("#1E1E1E"))
            holder.price.text = "${item.price} Coins"
        }

        holder.itemView.setOnClickListener { onTreeClick(item) }
    }

    override fun getItemCount() = items.size
}