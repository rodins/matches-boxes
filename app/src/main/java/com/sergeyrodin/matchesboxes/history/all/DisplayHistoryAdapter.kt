package com.sergeyrodin.matchesboxes.history.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayHistoryListItemBinding

class DisplayHistoryAdapter(private val displayHistoryListener: DisplayHistoryListener): ListAdapter<DisplayHistory, DisplayHistoryAdapter.ViewHolder>(DisplayHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), displayHistoryListener)
    }

    class ViewHolder private constructor(private val binding: DisplayHistoryListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(displayHistory: DisplayHistory, listener: DisplayHistoryListener) {
            binding.displayHistory = displayHistory
            binding.clickListener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DisplayHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DisplayHistoryListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}

class DisplayHistoryDiffCallback: DiffUtil.ItemCallback<DisplayHistory>() {
    override fun areItemsTheSame(oldItem: DisplayHistory, newItem: DisplayHistory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DisplayHistory, newItem: DisplayHistory): Boolean {
        return oldItem == newItem
    }
}