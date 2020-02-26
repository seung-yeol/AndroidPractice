package com.example.presentation

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class OSYViewPagerAdapter :
    ListAdapter<OSYViewPagerAdapter.ListItem, OSYViewPagerAdapter.OSYViewHolder>(DiffCallback) {
    private val items = LinkedList<ListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OSYViewHolder {
        return OSYViewHolder(
            FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        )
    }

    override fun onBindViewHolder(holder: OSYViewHolder, position: Int) {
        println("OSY OnBindViewHolder")
        (holder.itemView as FrameLayout).apply {
            removeAllViews()
            addView(getItem(position).view)
        }
    }

    fun addView(view: View?) {
        items.add(ListItem(view!!, view.id))
        submitList(items)
    }

    private object DiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
            oldItem.itemId == newItem.itemId

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean = false
    }

    data class ListItem(
        val view: View,
        val itemId: Int
    )

    class OSYViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}