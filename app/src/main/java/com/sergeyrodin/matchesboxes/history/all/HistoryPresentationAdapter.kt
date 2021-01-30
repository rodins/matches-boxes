package com.sergeyrodin.matchesboxes.history.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.DisplayHistoryListItemBinding
import com.sergeyrodin.matchesboxes.history.HistoryPresentation

class HistoryPresentationAdapter(
    private val clickListener: HistoryPresentationClickListener,
    private val longClickListener: HistoryPresentationClickListener
) : ListAdapter<HistoryPresentation, HistoryPresentationAdapter.ViewHolder>(HistoryPresentationDiffCallback()) {

    var highlightedItemId = -1
        set(value) {
            if(value != -1) {
                field = value
                val position = getPositionById(value)
                notifyItemChanged(position)
            }else {
                val position = getPositionById(field)
                field = value
                notifyItemChanged(position)
            }
        }

    private fun getPositionById(id: Int): Int {
        val item = currentList.find {
            it.id == id
        }
        return currentList.indexOf(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), highlightedItemId)
    }

    class ViewHolder private constructor(private val binding: DisplayHistoryListItemBinding,
                                         private val presentationClickListener: HistoryPresentationClickListener,
                                         private val longClickListener: HistoryPresentationClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(presentation: HistoryPresentation, highlightedItemId: Int) {
            setupClickListener(presentation.id)
            setupLongClickListener(presentation.id)
            setHistoryPresentation(presentation)

            if(highlightedItemId != presentation.id) {
                binding.historyItemLayout.setBackgroundResource(R.color.design_default_color_background)
            }else {
                binding.historyItemLayout.setBackgroundResource(R.color.secondaryLightColor)
            }

            binding.executePendingBindings()
        }

        private fun setupClickListener(id: Int) {
            binding.historyItemLayout.setOnClickListener {
                presentationClickListener.onClick(id)
            }
        }

        private fun setupLongClickListener(id: Int) {
            binding.historyItemLayout.setOnLongClickListener {
                longClickListener.onClick(id)
                true
            }
        }

        private fun setHistoryPresentation(presentation: HistoryPresentation) {
            binding.displayHistory = presentation
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

class HistoryPresentationClickListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
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