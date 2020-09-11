package com.sergeyrodin.matchesboxes.history.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayComponentHistoryListItemBinding

class DisplayComponentHistoryAdapter:
    ListAdapter<ComponentHistoryPresentation, DisplayComponentHistoryAdapter.ViewHolder>(DisplayComponentHistoryDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(private val binding: DisplayComponentHistoryListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(componentHistoryPresentation: ComponentHistoryPresentation) {
            binding.displayComponentHistory = componentHistoryPresentation
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DisplayComponentHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DisplayComponentHistoryDiffCallback: DiffUtil.ItemCallback<ComponentHistoryPresentation>() {

    override fun areItemsTheSame(
        oldItem: ComponentHistoryPresentation,
        newItem: ComponentHistoryPresentation
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ComponentHistoryPresentation,
        newItem: ComponentHistoryPresentation
    ): Boolean {
        return oldItem == newItem
    }
}