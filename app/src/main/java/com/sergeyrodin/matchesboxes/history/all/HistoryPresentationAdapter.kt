package com.sergeyrodin.matchesboxes.history.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayHistoryListItemBinding
import com.sergeyrodin.matchesboxes.history.HistoryPresentation

class HistoryPresentationAdapter(
    private val clickListener: HistoryPresentationClickListener,
    private val longClickListener: HistoryPresentationClickListener
) : ListAdapter<HistoryPresentation, HistoryPresentationAdapter.ViewHolder>(HistoryPresentationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ViewHolder private constructor(private val binding: DisplayHistoryListItemBinding,
                                         private val presentationClickListener: HistoryPresentationClickListener,
                                         private val longClickListener: HistoryPresentationClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var historyPresentation: HistoryPresentation

        fun bind(presentation: HistoryPresentation, position: Int
        ) {
            historyPresentation = presentation
            setupClickListener(position)
            setupLongClickListener(position)
            setupBinding()
        }

        private fun setupClickListener(position: Int) {
            binding.historyItemLayout.setOnClickListener {
                presentationClickListener.onClick(position)
            }
        }

        private fun setupLongClickListener(position: Int) {
            binding.historyItemLayout.setOnLongClickListener {
                longClickListener.onClick(position)
                true
            }
        }

        private fun setupBinding() {
            binding.apply{
                displayHistory = historyPresentation
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup,
                     clickListener: HistoryPresentationClickListener,
                     longClickListener: HistoryPresentationClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DisplayHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, clickListener, longClickListener)
            }
        }
    }
}

class HistoryPresentationClickListener(val clickListener: (position: Int) -> Unit) {
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