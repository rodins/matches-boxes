package com.sergeyrodin.matchesboxes.history.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayHistoryListItemBinding

class HistoryPresentationAdapter(
    private val historyPresentationClickListener: HistoryPresentationClickListener,
    private val historyPresentationLongClickListener: HistoryPresentationLongClickListener
) : ListAdapter<HistoryPresentation, HistoryPresentationAdapter.ViewHolder>(HistoryPresentationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position, historyPresentationClickListener, historyPresentationLongClickListener)
    }

    class ViewHolder private constructor(private val binding: DisplayHistoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var historyPresentation: HistoryPresentation

        fun bind(
            historyPresentation: HistoryPresentation,
            position: Int,
            presentationClickListener: HistoryPresentationClickListener,
            presentationLongClickListener: HistoryPresentationLongClickListener
        ) {
            this.historyPresentation = historyPresentation
            setupLongClickListener(position, presentationLongClickListener)
            setupBinding(presentationClickListener)
        }

        private fun setupLongClickListener(position: Int, longClickListener: HistoryPresentationLongClickListener) {
            binding.historyItemLayout.setOnLongClickListener {
                longClickListener.onClick(position)
                true
            }
        }

        private fun setupBinding(presentationClickListener: HistoryPresentationClickListener) {
            binding.apply{
                clickListener = presentationClickListener
                displayHistory = historyPresentation
                executePendingBindings()
            }
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

class HistoryPresentationClickListener(val clickListener: (presentation: HistoryPresentation) -> Unit) {
    fun onClick(presentation: HistoryPresentation) = clickListener(presentation)
}

class HistoryPresentationLongClickListener(val clickListener: (position: Int) -> Unit) {
    fun onClick(position: Int) = clickListener(position)
}

class HistoryPresentationDiffCallback : DiffUtil.ItemCallback<HistoryPresentation>() {
    override fun areItemsTheSame(
        oldItem: HistoryPresentation,
        newItem: HistoryPresentation
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: HistoryPresentation,
        newItem: HistoryPresentation
    ): Boolean {
        return oldItem == newItem
    }
}