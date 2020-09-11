package com.sergeyrodin.matchesboxes.history.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayHistoryListItemBinding

class DisplayHistoryAdapter(private val displayHistoryListener: DisplayHistoryListener): ListAdapter<HistoryPresentation, DisplayHistoryAdapter.ViewHolder>(DisplayHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), displayHistoryListener)
    }

    class ViewHolder private constructor(private val binding: DisplayHistoryListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(historyPresentation: HistoryPresentation, listener: DisplayHistoryListener) {
            binding.displayHistory = historyPresentation
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

class DisplayHistoryListener(val clickListener: (id: Int, name: String) -> Unit) {
    fun onClick(id: Int, name: String) = clickListener(id, name)
}

class DisplayHistoryDiffCallback: DiffUtil.ItemCallback<HistoryPresentation>() {
    override fun areItemsTheSame(oldItem: HistoryPresentation, newItem: HistoryPresentation): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistoryPresentation, newItem: HistoryPresentation): Boolean {
        return oldItem == newItem
    }
}