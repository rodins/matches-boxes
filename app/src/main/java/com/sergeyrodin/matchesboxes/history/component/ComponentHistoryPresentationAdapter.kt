package com.sergeyrodin.matchesboxes.history.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayComponentHistoryListItemBinding
import com.sergeyrodin.matchesboxes.history.HistoryPresentation
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationClickListener
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationDiffCallback

class DisplayComponentHistoryAdapter(private val longClickListener: HistoryPresentationClickListener,
                                     private val clickListener: ComponentHistoryPresentationClickListener):
    ListAdapter<HistoryPresentation, DisplayComponentHistoryAdapter.ViewHolder>(HistoryPresentationDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ViewHolder private constructor(
        private val binding: DisplayComponentHistoryListItemBinding,
        private val clickListener: ComponentHistoryPresentationClickListener,
        private val longClickListener: HistoryPresentationClickListener
    ):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(presentation: HistoryPresentation,
                 position: Int
        ) {
            setupLongClickListener(position)
            setupClickListener()
            setupPresentation(presentation)
            executePendingBindings()
        }

        private fun setupLongClickListener(position: Int) {
            binding.layout.setOnLongClickListener {
                longClickListener.onClick(position)
                true
            }
        }

        private fun setupPresentation(presentation: HistoryPresentation) {
            binding.displayComponentHistory = presentation
        }

        private fun setupClickListener() {
            binding.clickListener = clickListener
        }

        private fun executePendingBindings() {
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup,
                     clickListener: ComponentHistoryPresentationClickListener,
                     longClickListener: HistoryPresentationClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DisplayComponentHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, clickListener, longClickListener)
            }
        }
    }
}

class ComponentHistoryPresentationClickListener(private val listener: () -> Unit){
    fun onClick() = listener()
}