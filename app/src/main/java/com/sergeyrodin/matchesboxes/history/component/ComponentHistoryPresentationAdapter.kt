package com.sergeyrodin.matchesboxes.history.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayComponentHistoryListItemBinding
import com.sergeyrodin.matchesboxes.history.HistoryPresentation
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationLongClickListener

class DisplayComponentHistoryAdapter(private val longClickListener: HistoryPresentationLongClickListener,
                                     private val clickListener: ComponentHistoryPresentationClickListener):
    ListAdapter<HistoryPresentation, DisplayComponentHistoryAdapter.ViewHolder>(DisplayComponentHistoryDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position, longClickListener, clickListener)
    }

    class ViewHolder private constructor(private val binding: DisplayComponentHistoryListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(presentation: HistoryPresentation,
                 position: Int,
                 longClickListener: HistoryPresentationLongClickListener,
                 clickListener: ComponentHistoryPresentationClickListener) {
            setupLongClickListener(longClickListener, position)
            setupClickListener(clickListener)
            setupPresentation(presentation)
            executePendingBindings()
        }

        private fun setupLongClickListener(
            longClickListener: HistoryPresentationLongClickListener,
            position: Int
        ) {
            binding.layout.setOnLongClickListener {
                longClickListener.onClick(position)
                true
            }
        }

        private fun setupPresentation(presentation: HistoryPresentation) {
            binding.displayComponentHistory = presentation
        }

        private fun setupClickListener(clickListener: ComponentHistoryPresentationClickListener) {
            binding.clickListener = clickListener
        }

        private fun executePendingBindings() {
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

class ComponentHistoryPresentationClickListener(private val listener: () -> Unit){
    fun onClick() = listener()
}

class DisplayComponentHistoryDiffCallback: DiffUtil.ItemCallback<HistoryPresentation>() {

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