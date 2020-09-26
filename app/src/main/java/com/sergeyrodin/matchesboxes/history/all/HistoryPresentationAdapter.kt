package com.sergeyrodin.matchesboxes.history.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayHistoryListItemBinding

class HistoryPresentationAdapter(
    private val clickListener: HistoryPresentationClickListener,
    private val longClickListener: HistoryPresentationLongClickListener
) : ListAdapter<HistoryPresentation, HistoryPresentationAdapter.ViewHolder>(HistoryPresentationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ViewHolder private constructor(private val binding: DisplayHistoryListItemBinding,
                                         private val presentationClickListener: HistoryPresentationClickListener,
                                         private val longClickListener: HistoryPresentationLongClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var historyPresentation: HistoryPresentation

        fun bind(presentation: HistoryPresentation, position: Int
        ) {
            historyPresentation = presentation
            setupLongClickListener(position)
            setupBinding()
        }

        private fun setupLongClickListener(position: Int) {
            binding.historyItemLayout.setOnLongClickListener {
                longClickListener.onClick(position)
                true
            }
        }

        private fun setupBinding() {
            binding.apply{
                clickListener = presentationClickListener
                displayHistory = historyPresentation
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup,
                     clickListener: HistoryPresentationClickListener,
                     longClickListener: HistoryPresentationLongClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DisplayHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, clickListener, longClickListener)
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